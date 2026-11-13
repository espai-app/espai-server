<?php

// get data
$season = app\espai\wordpress\Season::get();
$seasonStartDate = new DateTime($season->start);
$seasonEndDate = new DateTime($season->end);
$startTime = '08:00';
$endTime = '12:00';

$venueSlug = get_query_var('espai_venue_id');
$venue = \app\espai\wordpress\SeasonVenue::getById($venueSlug);
$hallList = \app\espai\wordpress\Hall::listForVenue($venueSlug);

$productionId = get_query_var('espai_production_id');
$production = app\espai\wordpress\Production::getById($productionId);
$espai_title = "Terminwunsch: " . $production->title;

// create reservation object if necessary
if (!isset($_SESSION['reservation'])) {
  $_SESSION['reservation'] = new \app\espai\wordpress\Reservation();
  $_SESSION['reservation']->event = new \app\espai\wordpress\Event();
}
$reservation = $_SESSION['reservation'];
$reservation->event->production = $production;
$reservation->event->venue = $venue;
$reservation->event->status = 'REQUESTED';

// validate on postback
$errors = [];
$requiredFields = ['date', 'time', 'hallId', 'versionId'];
if (isset($_POST['date'])) {

  foreach ($requiredFields as $f) {
    if (!isset($_POST[$f]) || empty($_POST[$f]) || trim($_POST[$f]) == '') {
      $errors[$f] = "Dies ist ein Pflichtfeld.";
    }
  }

  // validate date
  $requestedDate = new DateTime($_POST['date']);


  if (($requestedDate < $seasonStartDate || $requestedDate > $seasonEndDate) && !isset($errors['date'])) {
      $errors['date'] = 'Veranstaltungen können nur zwischen dem ' . $seasonStartDate->format('d.m.Y') . ' und dem ' . $seasonEndDate->format('d.m.Y') . ' stattfinden.';
  }

  // validate time
  $requestedTime = new DateTime($_POST['time']);
  $allowedStartTime = new DateTime($startTime);
  $allowedEndTime = new DateTime($endTime);

  if (($requestedTime < $allowedStartTime || $requestedTime > $allowedEndTime) && !isset($errors['time'])) {
    $errors['time'] = 'Veranstaltungen können nur zwischen ' . $allowedStartTime->format('H:i') . ' Uhr und ' . $allowedEndTime->format('H:i') . ' Uhr stattfinden.';
  }

  // validate hall
  $hallId = filter_input(INPUT_POST, 'hallId', FILTER_SANITIZE_NUMBER_INT);
  $hall = null;
  foreach ($hallList as $h) {
    if ($h->id == $hallId) {
      $hall = $h;
      break;
    }
  }

  if ($hall === null && !isset($errors['hallId'])) {
    $errors['hallId'] = 'Der Saal konnte nicht gefunden werden.';
  }

  // validate version
  $versionId = filter_input(INPUT_POST, 'versionId', FILTER_SANITIZE_NUMBER_INT);
  $version = null;
  foreach ($production->versions as $v) {
    if ($v->id == $versionId) {
      $version = $v;
      break;
    }
  }

  if ($version === null && !isset($errors['versionId'])) {
    $errors['versionId'] = 'Die Sprachfassung konnte nicht gefunden werden.';
  }

  // save input to session
  $reservation->event->date = $requestedDate->format("yyyy-MM-dd");
  $reservation->event->time = $requestedTime->format("HH:mm") . ':00.00';
  $reservation->event->hall = $hall;
  $reservation->event->version = $version;


  // validation passed, move on to next step
  if (empty($errors)) {
    $reservation->event = app\espai\wordpress\Event::create($reservation->event);
    $_SESSION['reservation'] = $reservation;
    
    header('Location: ' . trailingslashit(get_home_url()) . 'buchen/adresse');
    exit;
  }
}
?>

<?php get_header(); ?>

<div class="espai-content">

<h1><?= $espai_title ?></h1>

<form method="POST" class="espai-form">

  <div>
    <div class="field">
      <label for="date">Datum*</label>
      <input type="date"
             name="date"
             id="date"
             value="<?= !empty($reservation->event->date) ? esc_attr($reservation->event->date) : '' ?>"
             min="$season->start"
             max="$season->end" />
      <p class="hint">Termine sind Montag bis Freitag zwischen dem <?= $seasonStartDate->format('d.m.Y') ?> und dem <?= $seasonEndDate->format('d.m.Y') ?> möglich.</p>
      <?php if (isset($errors['date'])) { ?><p class="validation-error"><?= $errors['date'] ?></p><?php } ?>
    </div>

    <div class="field">
      <label for="time">Zeit*</label>
      <input type="time"
             name="time"
             id="time"
             value="<?= !empty($reservation->event->time) ? esc_attr($reservation->event->time) : '' ?>"
             min="<?= $startTime ?>"
             max="<?= $endTime ?>" />
      <p class="hint">Vorstellungsbeginn ist zwischen <?= $startTime ?> Uhr und <?= $endTime ?> Uhr möglich.</p>
      <?php if (isset($errors['time'])) { ?><p class="validation-error"><?= $errors['time'] ?></p><?php } ?>
    </div>

    <?php if (count($production->versions) === 1) {?>
      <div class="field">
        <label for="presentation">Vorstellung</label>
        <input type="text"
               name="presentation"
               id="presentation"
               value="<?= $production->title ?><?= !empty($production->versions[0]->versionName) ? ' (' . esc_attr($production->versions[0]->versionName) . ')' : '' ?>"
               readonly />
        <input type="hidden" name="versionId" value="<?= $production->versions[0]->id ?>" />
      </div>
    <?php } ?>

    <?php if (count($hallList) === 1) {?>
      <div class="field">
        <label for="venue">Ort</label>
        <input type="text"
               name="venue"
               id="venue"
               value="<?= $reservation->event->venue->name . ', ' . $reservation->event->venue->city ?>"
               readonly />
        <input type="hidden" name="hallId" value="<?= $hallList[0]->id ?>" />
      </div>
    <?php } ?>

    <?php if (count($hallList) > 1) {?>
      <div class="field">
        <div class="mt-2 mb-1"><b>Saalkapazität</b></div>

        <p>Bitte wählen Sie einen Saal, der zu Ihrem Terminwunsch passt.</p>

        <?php if (isset($errors['hallId'])) { ?><p class="validation-error"><?= $errors['hallId'] ?></p><?php } ?>

        <?php foreach ($hallList as $h) { ?>
          <div class="input-radio">
            <input type="radio" id="hallId-<?= $h->id ?>" name="hallId" value="<?= $h->id ?>" <?= $reservation->event->hall != null && $reservation->event->hall->id === $h->id ? 'checked' : '' ?> />
            <label for="hallId-<?= $h->id ?>"><?= esc_html($h->name) ?></label>
            <?php
              $capacities = [];
              foreach($h->hallCapacities as $c) {
                $capacities[] = $c->capacity  . ' × ' . $c->seatCategoryName;
              }
            ?>
            <p><?= implode(", ", $capacities) ?></p>
          </div>
        <?php } ?>
      </div>
    <?php } ?>

    <?php if (count($production->versions) > 1) {?>
      <div class="field">
        <div class="mt-2 mb-1"><b>Sprachfassung</b></div>

        <p>Bitte wählen Sie eine Sprachfassung.</p>

        <?php if (isset($errors['versionId'])) { ?><p class="validation-error"><?= $errors['versionId'] ?></p><?php } ?>

        <?php foreach ($production->versions as $v) { ?>
          <div class="input-radio">
            <input type="radio" id="versionId-<?= $v->id ?>" name="versionId" value="<?= $v->id ?>" <?= $reservation->event->version != null && $reservation->event->version->id === $v->id ? 'checked' : '' ?> />
            <label for="versionId-<?= $v->id ?>"><?= esc_html($v->versionName === '' ? 'deutsche Fassung' : $v->versionName) ?></label>
          </div>
        <?php } ?>
      </div>
    <?php } ?>

    <div class="mt-5">
      <button type="submit">weiter ›</button>
    </div>
  </div>
</form>

<?php get_footer(); ?>
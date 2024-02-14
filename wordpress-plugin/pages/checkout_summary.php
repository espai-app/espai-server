<?php
$reservation = $_SESSION['reservation'];
$espai_title = 'Zusammenfassung der Reservierung';

$event = $reservation->event;

$totalMinutes = $event->production->durationInMinutes;

$errors = [];

if (isset($_POST['action'])) {
  
  // validate
  if (empty($_POST['tos'])) {
    $errors['tos'] = 'Bitte akzeptieren Sie die AGB um fortzufahren.';
  } else {
    $reservation->acceptTos = true;
  }
  
  if (!empty($_POST['newsletter'])) {
    $reservation->extras['Newsletter'] = "ja";
  }
  
  if (empty($errors)) {
    $reservation_transfer = json_decode(json_encode($reservation));
    $reservation_transfer->event = $reservation->event->id;
    
    $errors = \app\espai\wordpress\Reservation::validate($reservation_transfer);
    
    if (empty($errors)) {
      \app\espai\wordpress\Reservation::send($reservation_transfer);
      unset($_SESSION['reservation']);
      
      header('Location: ' . trailingslashit(get_home_url()) . 'buchen/danke');
      exit;
    }
  }
}

?>

<?php get_header(); ?>

<div class="espai-content">
<form method="POST" class="espai-form">
  <h1>Zusammenfassung</h1>

  <p>Bitte prüfen Sie Ihre Buchung noch einmal auf Vollständigkeit und Richtigkeit.</p>

  <h3>Ihre Vorstellung</h3>

  <div><?= $event->title ?> <?= !empty($event->version) ? ' (' . $event->version . ')' : '' ?></div>
  <div>Beginn <?= \app\espai\wordpress\EspaiPlugin::formatDate($event->date) ?>, <?= \app\espai\wordpress\EspaiPlugin::formatTime($event->time) ?> Uhr</div>
  <div>Laufzeit Film <?= $event->production->durationInMinutes ?> min.</div>
  <div><?= $event->venue->name ?>, <?= $event->venue->address ?>, <?= $event->venue->postcode ?> <?= $event->venue->city ?></div>

  <?php if (!empty($event->childEvents) && !empty($reservation->childEvents)) { ?>
    <?php foreach ($event->childEvents as $child) { ?>
      <?php if (in_array($child->id, $reservation->childEvents)) { ?>
        <div><?= $child->title ?> <?= !empty($child->version) ? ' (' . $child->version . ')' : '' ?></div>
        <div>Dauer: <?= $child->production->durationInMinutes ?> min.</div>
      <?php } ?>
    <?php } ?>
  <?php } ?>

  <div>
    <a href="<?= esc_attr(trailingslashit(get_home_url())) ?>/buchen/veranstaltung/<?= \app\espai\wordpress\EspaiPlugin::slugify($event->title) ?>-<?= $event->id ?>">Tickets bearbeiten</a>
  </div>

  <h3>Persönliche Daten</h3>

  <?= $reservation->company ?><br />
  <?= $reservation->givenName ?> <?= $reservation->surname ?><br />
  <?= $reservation->address ?><br />
  <?= $reservation->postcode ?> <?= $reservation->city ?><br /><br />

  Telefon: <?= $reservation->phone ?><br />
  E-Mail: <?= $reservation->email ?><br />

  <div class="mt-1 mb-3">
    <a href="<?= esc_attr(trailingslashit(get_home_url())) ?>/buchen/adresse">Adresse korrigieren</a>
  </div>

  <div>
    <label>
      <input type="checkbox" name="tos" value="true" /> Ich akzeptiere die <a href="<?= esc_attr(trailingslashit(get_home_url())) ?>agb" target="_blank">Allgemeinen Geschäftsbedingungen</a>.
    </label>
    
    <?php if (isset($errors['tos'])) { ?><p class="validation-error"><?= $errors['tos'] ?></p><?php } ?>
  </div>

  <div>
    <label>
      <input type="checkbox" name="newsletter" value="true" /> Ich möchte den Newsletter des Objektiv e.V. mit Informationen zu medienpädagogischen Themen und Weiterbildungen erhalten.
    </label>
  </div>

  <div class="mt-3">
    <button type="submit" name="action" value="submit">zahlungspflichtig bestellen</button>
  </div>
</form>
</div>

<?php get_footer(); ?>


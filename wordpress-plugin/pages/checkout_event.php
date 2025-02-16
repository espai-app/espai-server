<?php
$eventId = get_query_var('espai_event_id');
$event = \app\espai\wordpress\Event::get($eventId);
$production = app\espai\wordpress\Production::getById($event->production->id);
$espai = app\espai\wordpress\Espai::getInstance();
$espai_title = 'Tickets auswählen';

if (!isset($_SESSION['reservation'])) {
  $_SESSION['reservation'] = new \app\espai\wordpress\Reservation();
}
$reservation = $_SESSION['reservation'];
$errors = [];

if (isset($_POST['tickets'])) {

  $reservation->event = $event;
  $reservation->tickets = $_POST['tickets'];

  // validate ticket amounts
  $errors = \app\espai\wordpress\Reservation::checkTicketAvailability($reservation);
//  $total = 0;
//  foreach ($_POST['tickets'] as $t) {
//    $total += $t;
//  }
//
//  if ($total <= 0) {
//    $errors['tickets'] = "Bitte reservieren Sie min. ein Ticket.";
//  }
//
//  if ($total > $event->availableTickets) {
//    $errors['tickets'] = "Die gewünschte Anzahl an Tickets übersteigt die Anzahl der verfügbaren Sitzplätze. Sprechen Sie uns gern an. Manchmal ist eine zusätzliche Vorstellung oder ein Wechsel in einen größeren Saal möglich.";
//  }
//
//  if (!empty($_POST['childEvents'])) {
//    $reservation->childEvents = $_POST['childEvents'];
//
//    foreach ($_POST['childEvents'] as $childId) {
//      foreach ($event->childEvents as $child) {
//        if ($child->id == $childId && $child->availableTickets < $total && !$child->mandatory) {
//
//          $errors['childEvents'] = "Die gewünschte Teilnehmeranzahl übersteigt maximale Kapazität des Filmgespräches.";
//          if ($total <= $event->availableTickets) {
//            $errors['childEvents'] .= " Wenn Sie mögen, können Sie den Film dennoch schauen. Sie müssen aber nach der Vorführung den Saal verlassen.";
//          }
//        }
//      }
//    }
//  }

  // save to session
  $_SESSION['reservation'] = $reservation;

  if (empty($errors)) {
    header('Location: ' . trailingslashit(get_home_url()) . 'buchen/adresse');
    exit;
  }
}

?>

<?php get_header(); ?>

<div class="espai-content">

<h1><?= $event->title ?></h1>

<div class="grid">
  <div class="lg:col-4 md:col-3 col-12">
    <?php if (!empty($production->attachments['Poster'])) { ?>
      <img class="img-responsive" src="<?= trailingslashit($espai->baseUrl) ?>webservice/attachments/<?= $production->attachments['Poster'][0]->id ?>/datastream" alt="<?= esc_attr($production->attachments['Poster'][0]->caption) ?>" />
    <?php } ?>
  </div>
  <div class="lg:col-8 md:col-9 col-12">
    <?= $production->description ?>

    <div class="mt-3">
      Laufzeit: <?= $event->production->durationInMinutes ?> min.
    </div>
  </div>


</div>

<form method="POST" class="espai-form">

  <h3>Tickets reservieren</h3>

  <p>Verfügbare Plätze:
  <?php foreach ($event->availableTickets as $key => $value) { ?>
    <br /><?= $key ?>: <?= $value ?>
  <?php } ?>
  </p>

  <?php if (isset($errors)) { ?>
    <?php foreach ($errors as $error) { ?>
      <p class="validation-error"><?= $error ?></p>
    <?php } ?>
  <?php } ?>

  <table>
    <thead>
      <tr>
        <th>Anz.</th>
        <th>Platzkategorie</th>
        <th>Preiskategorie</th>
        <th>Preis</th>
      </tr>
    </thead>
    <tbody>
      <?php foreach ($event->prices as $price) { ?>
        <tr>
          <td><input type="number" name="tickets[<?= $price->id ?>]" value="<?= isset($reservation->tickets[$price->id]) ? $reservation->tickets[$price->id] : '0' ?>" /></td>
          <td><?= $price->seatCategory ?></td>
          <td><?= $price->priceCategory ?></td>
          <td><?= number_format($price->price / 100, 2, ',', '.') ?> €</td>
        </tr>
      <?php } ?>
    </tbody>
  </table>

  <p>Der Eintritt ist direkt im Kino zu bezahlen. Bitte sammeln Sie das Geld vorher ein um Verzögerungen beim Einlass zu vermeiden.</p>

  <?php if (!empty($event->childEvents)) { ?>
    <h3>Zusatzangebote</h3>
    <p>Zu dieser Veranstaltung bieten wir folgende Zusatzangebote an.</p>

    <?php if (isset($errors['tickets'])) { ?>
      <p class="validation-error"><?= $errors['childEvents'] ?></p>
    <?php } ?>

    <ul class="espai-event-child-events">
      <?php foreach ($event->childEvents as $child) { ?>
      <?php if ($child->availableTickets > 0 || $child->mandatory) { ?>
      <li>
        <input type="<?= $child->mandatory ? 'hidden' : 'checkbox' ?>" name="childEvents[]" value="<?= $child->id ?>" <?= in_array($child->id, $reservation->childEvents) || $child->mandatory ? 'checked' : '' ?> />
        <h3><?= $child->title ?></h3>
        <div><?= $child->production->description ?></div>
        <p>Dauer: <?= $child->production->durationInMinutes ?> min.</p>
        <p>Verfügbare Plätze:
        <?php foreach ($child->availableTickets as $key => $value) { ?>
          <br /><?= $key ?>: <?= $value ?>
        <?php } ?>
        </p>
        <?php if ($child->mandatory) { ?><p class="hint-danger">Dieses Zusatzangebot ist fester Bestandteil der Veranstaltung und kann nicht abgewählt werden.</p><?php } ?>
      </li>
      <?php } ?>
      <?php } ?>
    </ul>
    <p class="hint-danger">Bitte beachten Sie, dass sich Ihr Kinobesuch um die angegebene Zeit verlängert.</p>
  <?php } ?>

  <div>
    <button type="submit">weiter ›</button>
  </div>
</form>

</div>

<?php get_footer(); ?>

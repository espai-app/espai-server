<?php

$venueId = get_query_var('espai_venue_id');
$venue = \app\espai\wordpress\SeasonVenue::getById($venueId);

$espai_title = $venue->name . ', ' . $venue->city;

if ($venue == null) {
  header("HTTP/1.0 404 Not Found");
  return;
}

$eventList = \app\espai\wordpress\Event::listForVenue($venueId);

?>
<?php get_header(); ?>

<div class="espai-content">
  <h2><?= $venue->name ?></h2>

  <div class="grid">
    <div class="col-12 md:col-8">
      <?= $venue->address ?><br />
      <?= $venue->postcode ?> <?= $venue->city ?><br /><br />
      Telefon: <?= $venue->phone ?><br />
      E-Mail: <?= $venue->email ?>
      <?php if ($venue->publicNotes) { ?>
        <div class="espai-venue-public-notes">
          <?= $venue->publicNotes ?>
        </div>
        <div class="espai-venue-season-notes">
          <?= $venue->seasonNotes ?>
        </div>
      <?php } ?>
    </div>

    <div class="col-12 md:col-4">
      <img src="<?= \app\espai\wordpress\Venue::getImage($venueId) ?>" alt="Logo <?= esc_attr($venue->name) ?>" class="img-responsive" />
    </div>
  </div>

  <h3>Veranstaltungen in diesem Kino</h3>

  <p>Für eine bessere Planbarkeit seitens der Veranstaltungsorte bitten wir Hort- und Kitagruppen um eine Reservierung. Diese können Sie ganz einfach über unser Buchungsformular vornehmen. Private Besucher:innen
  können sich ihre Tickets direkt vor Ort kaufen. Sollten Sie dennoch eine Reservierung wünschen, wenden Sie sich bitte direkt an den Veranstaltungsort.</p>

  <?php \app\espai\wordpress\EventListShortcode::render($eventList, false, true) ?>
</div>

<?php get_footer(); ?>


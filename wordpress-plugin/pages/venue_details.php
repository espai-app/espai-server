<?php

$venueId = get_query_var('espai_venue_id');
$venue = \app\espai\wordpress\Venue::getById($venueId);

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

  <h3>Veranstaltungen in diesem Kino</h3>
  <?php \app\espai\wordpress\EventListShortcode::render($eventList, false, true) ?>
</div>

<?php get_footer(); ?>


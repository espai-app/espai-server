<?php

$venueSlug = get_query_var('espai_venue_id');
$venue = \app\espai\wordpress\SeasonVenue::getById($venueSlug);

$venueProductions = [];
if ($venue->allowFreeBooking) {
  $venueProductions = \app\espai\wordpress\Production::listForVenue($venueSlug);
}

$espai_title = $venue->name . ', ' . $venue->city;

if ($venue == null) {
  header("HTTP/1.0 404 Not Found");
  return;
}

$eventList = \app\espai\wordpress\Event::listForVenue($venueSlug);

?>
<?php get_header(); ?>

<div class="espai-content">
  <h1><?= $venue->name ?></h1>

  <div class="grid">
    <div class="col-12 md:col-8">
      <?= $venue->address ?><br />
      <?= $venue->postcode ?> <?= $venue->city ?><br /><br />
	    <?php if (!empty($venue->phone)) { ?>
        <p>Telefon: <a href="tel:<?= $venue->phone ?>"><?= $venue->phone ?></a></p>
	    <?php } ?>
      <?php if (!empty($venue->email)) { ?>
        <p>Email: <a href="mailto:<?= $venue->email ?>"><?= $venue->email ?></a></p>
	    <?php } ?>

      <?php if ($venue->publicNotes) { ?>
        <div class="espai-venue-public-notes">
          <?= $venue->publicNotes ?>
        </div>
      <?php } ?>

      <?php if ($venue->seasonNotes) { ?>
        <div class="espai-venue-public-notes">
          <?= $venue->seasonNotes ?>
        </div>
      <?php } ?>
    </div>

    <div class="col-12 md:col-4">
      <img src="<?= \app\espai\wordpress\Venue::getImage($venue->id) ?>" alt="Logo <?= esc_attr($venue->name) ?>" class="img-responsive" />
    </div>
  </div>

  <h2 class="mt-3">Veranstaltungen an diesem Ort</h2>

  <?php \app\espai\wordpress\EventListShortcode::render($eventList, false, true) ?>

  <?php if ($venue->allowFreeBooking) { ?>
    <h2 class="mt-3">Terminwunsch</h2>
    <p>Für die folgenden Filme können Sie einen individuellen Terminwunsch anfragen.</p>

    <div class="grid">
      <?php foreach($venueProductions as $p) { ?>
        <div class="col-12 md:col-6">
          <div class="grid">
            <div class="col-3">
              <img src="<?= \app\espai\wordpress\Production::getPoster($p->id) ?>" alt="Poster von <?= esc_attr($p->title) ?>" class="img-responsive" />
            </div>
            <div class="col-9">
              <h3><?= $p->title ?></h3>

              <div class="mt-3 mb-3">
                <a href="/buchen/terminwunsch/<?= esc_attr($venue->slug) ?>/<?= esc_attr($p->id) ?>" class="button">Termin anfragen</a>
              </div>

              <p><?= $p->description ?></p>

              <p>Altersempfehlung: <?= $p->recommendedAge ?>

            </div>
          </div>
        </div>
      <?php } ?>
    </div>
  <?php } ?>


</div>

<?php get_footer(); ?>


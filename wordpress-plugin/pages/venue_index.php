<?php
$venues = \app\espai\wordpress\Venue::list();
$espai_title = 'Teilnehmende Kinos';
?>
<?php get_header(); ?>

<div class="espai-content">
  <h2>Veranstaltungsorte</h2>

  <?php if (empty($venues)) { ?>
    <p class="empty">Keine Veranstaltungsorte gefunden.</p>
  <?php } else {

    // order by city
    $venueMap = [];
    foreach ($venues as $v) {
      if (!isset($venueMap[$v->city])) {
        $venueMap[$v->city] = [];
      }
      $venueMap[$v->city][] = $v;
    }

    ksort($venueMap);

    foreach ($venueMap as $city => $venueList) { ?>

      <h3><?= $city ?></h3>

      <?php usort($venueList, array('\app\espai\wordpress\Venue', 'compare')) ?>

      <ul class="espai-venue-list">
        <?php foreach ($venueList as $venue) { ?>
          <li>
            <a href="<?= trailingslashit(get_home_url()) ?>orte/<?= \app\espai\wordpress\EspaiPlugin::slugify($venue->name . ' ' . $venue->city) ?>-<?= $venue->id ?>">
              <img src="<?= \app\espai\wordpress\Venue::getImage($venue->id) ?>" alt="Logo <?= esc_attr($venue->name) ?>" class="img-responsive" />
              <?= $venue->name ?>
            </a>
          </li>
        <?php } ?>
      </ul>
    <?php } ?>
  <?php } ?>
</div>
<?php get_footer(); ?>
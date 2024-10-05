<?php

$productionId = get_query_var('espai_production_id');
$highlight_id = $_GET['highlight'];
$production = app\espai\wordpress\Production::getById($productionId);
$espai_title = $production->title;

$events = \app\espai\wordpress\Event::listForProduction($productionId);

$espai = app\espai\wordpress\Espai::getInstance();
?>

<?php get_header(); ?>

<div class="espai-content">
<h2><?= $production->title ?></h2>

<div class="grid">
  <div class="lg:col-4 md:col-3 col-12">
    <?php if (!empty($production->attachments['Poster'])) { ?>
      <img class="img-responsive" src="<?= trailingslashit($espai->baseUrl) ?>webservice/attachments/<?= $production->attachments['Poster'][0]->id ?>/datastream" alt="<?= esc_attr($production->attachments['Poster'][0]->caption) ?>" />
    <?php } ?>
  </div>
  <div class="lg:col-8 md:col-9 col-12">
    <?= $production->description ?>

    <dl class="espai-production-meta">
      <dt>Laufzeit</dt>
      <dd><?= $production->durationInMinutes ?> min.</dd>

      <dt>Altersfreigabe</dt>
      <dd><?= $production->rating ?></dd>

      <dt>Produktion</dt>
      <dd><?= $production->productionCountries ?>, <?= $production->productionYear ?></dd>

      <dt>Regie</dt>
      <dd><?= $production->director ?></dd>

      <dt>Cast</dt>
      <dd><?= $production->starring ?></dd>

      <dt>Buch</dt>
      <dd><?= $production->book ?></dd>
    </dl>
  </div>
</div>

<div class="grid">
  <?php
  $tagCategories = ['Genre', 'Themen', 'Trigger-Warnungen', 'Altersempfehlung'];
  foreach ($tagCategories as $tc) {

    if (!empty($production->tags[$tc])) {
      echo '<div class="md:col-3 col-12">';
      echo '<h3>' . $tc . '</h3>';
      echo join(", ", $production->tags[$tc]);
      echo '</div>';
    }
  }
  ?>
</div>

<div>
  <?php
  $attachCategories = ['externe Links', 'Downloads'];
  foreach ($attachCategories as $ac) {
    if (!empty($production->attachments[$ac])) {
      echo '<h3>' . $ac . '</h3>';
      echo '<ul>';
      foreach ($production->attachments[$ac] as $a) {
        echo '<li><a href="' .esc_attr($a->location) .'" target="_blank">' . esc_html($a->caption) . '</a></li>';
      }
      echo '</ul>';
    }
  }
  ?>
</div>

<?php if (!empty($production->attachments['Trailer'])) { ?>
<div class="video-container">
  <iframe src="<?= str_replace("watch?v=", "embed/", $production->attachments['Trailer'][0]->location) ?>" title="Video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>
</div>
<?php } ?>

<h2>Hier im Kino</h2>

<p>Für eine bessere Planbarkeit seitens der Veranstaltungsorte bitten wir Hort- und Kitagruppen um eine Reservierung. Diese können Sie ganz einfach über unser Buchungsformular vornehmen. Private Besucher:innen
  können sich ihre Tickets direkt vor Ort kaufen. Sollten Sie dennoch eine Reservierung wünschen, wenden Sie sich bitte direkt an den Veranstaltungsort.</p>

<?php \app\espai\wordpress\EventListShortcode::render($events, true, false, $highlight_id); ?>
</div>
<?php get_footer();
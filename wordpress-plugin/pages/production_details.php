<?php

$productionId = get_query_var('espai_production_id');
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
      <img class="img-responsive" src="<?= trailingslashit($espai->baseUrl) ?>webservice/attachments/<?= $production->attachments['Poster'][0]->id ?>/dataStream" alt="<?= esc_attr($production->attachments['Poster'][0]->caption) ?>" />
    <?php } ?>
  </div>
  <div class="lg:col-8 md:col-9 col-12">
    <?= $production->description ?>
  </div>
</div>

<div class="grid">
  <?php
  $tagCategories = ['Genre', 'Themen', 'Trigger-Warnungen', 'Altersempfehlung'];
  foreach ($tagCategories as $tc) {
    
    if (!empty($production->tags[$tc])) {
      echo '<div class="md:col-4 col-12">';
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
      foreach ($production->attachments[$ac] as $a) {
        echo '<a href="' .esc_attr($a->location) .'" target="_blank">' . esc_html($a->caption) . '</a>';
      }
    }
  }
  ?>
</div>

<?php if (!empty($production->attachments['Trailer'])) { ?>
<div class="video-container"> 
  <iframe src="<?= $production->attachments['Trailer'][0]->location ?>"></iframe>
</div>
<?php } ?>

<h2>Hier im Kino</h2>
<p>Suchen Sie sich hier eine Veranstaltung in Ihrer Nähe aus und klicken Sie zum Buchen auf den Button am Ende der Zeile.</p>

<?php \app\espai\wordpress\EventListShortcode::render($events, true, false); ?>
</div>
<?php get_footer(); 
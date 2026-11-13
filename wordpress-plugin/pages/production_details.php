<?php

$productionId = get_query_var('espai_production_id');
$highlight_id = isset($_GET['highlight']) ? $_GET['highlight'] : -1;
$production = app\espai\wordpress\Production::getById($productionId);
$espai_title = $production->title;

$events = \app\espai\wordpress\Event::listForProduction($productionId);

$espai = app\espai\wordpress\Espai::getInstance();
?>

<?php get_header(); ?>

<div class="espai-content">
<h2><?= $production->title ?></h2>

<!--
<?php var_dump($production); ?>
-->

<div class="grid">
  <div class="lg:col-4 md:col-3 col-12">
    <?php if (!empty($production->attachments['Poster'])) { ?>
      <img class="img-responsive" src="<?= trailingslashit($espai->baseUrl) ?>webservice/attachments/thumbnail/<?= $production->attachments['Poster'][0]->id ?>/datastream" alt="<?= esc_attr($production->attachments['Poster'][0]->caption) ?>" />
  <div class="espai-copyright"><?= $production->attachments['Poster'][0]->copyright ?></div>
    <?php } ?>
  </div>
  <div class="lg:col-8 md:col-9 col-12">
    <?= $production->description ?>

    <dl class="espai-production-meta">

	  <?php foreach($production->metadata as $key => $value) { ?>
      <?php if ($key !== "Viki-ID" && $value !== null && $value !== "" && !str_starts_with($value, "[")) { ?>
	      <dt><?= $key ?></dt>
	      <dd>
          <?php
            if ($value == "true" || $value == "false") {
      	      echo $value == "true" ? 'ja' : 'nein';
      		  } else {
      	      echo $value;
      		  }
      		?>
	      </dd>
      <?php } ?>
	  <?php } ?>
	</dl>
	<?php if (!empty($production->gretaAndStarks)) { ?>
     <img class="greta" src="<?= trailingslashit(plugin_dir_url(ESPAI_PLUGIN)) ?>assets/greta-small.png" alt="Greta &amp; Stark Logo" />
	<?php } ?>

  </div>
</div>

<div class="grid">
  <?php
  $tagCategories = ['Genre', 'Themen', 'Trigger-Warnungen', 'Altersempfehlung'];
  foreach ($tagCategories as $tc) {

    if (!empty($production->tags[$tc])) {
      echo '<div class="md:col-3 col-12">
	  <h3>' . $tc . '</h3>'. join(", ", $production->tags[$tc]).
	  '</div>';
    }
  }
  ?>
</div>

<div>
  <?php
  $attachCategories = ['externe Links', 'Downloads'];
  foreach ($attachCategories as $ac) {
    if (!empty($production->attachments[$ac])) {
      echo '<div class="espai-attlist"><h3>' . $ac . '</h3>';
      echo '<ul>';
      foreach ($production->attachments[$ac] as $a) {
        echo '<li><a href="' .esc_attr($a->location) .'" target="_blank">' . esc_html($a->caption) . '</a></li>';
      }
      echo '</ul>
	  </div>';
    }
  }
  ?>
</div>

<?php if (!empty($production->attachments['Trailer'])) { ?>
  <div class="video-container">
    <?php
            $video_src = $production->attachments['Trailer'][0]->location;
            if (strpos($video_src, "watch") > -1) {
                    $video_src = str_replace("watch?v=", "embed/", $production->attachments['Trailer'][0]->location);
            } elseif (strpos($video_src, "youtu.be") > -1) {
                    $video_src = "https://www.youtube.com/embed" . substr($video_src, 16);
            }
    ?>
    <iframe src="<?= $video_src ?>" title="Video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>
  </div>
<?php } ?>

<h2>Hier im Kino</h2>

<?php \app\espai\wordpress\EventListShortcode::render($events, true, false, $highlight_id); ?>
</div>
<?php get_footer();

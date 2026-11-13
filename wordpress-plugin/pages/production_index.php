<?php
$productions = \app\espai\wordpress\Production::list(1);
$spai_title = "Filmauswahl";
$espai = app\espai\wordpress\Espai::getInstance();
?>
<?php get_header(); ?>

<div class="espai-content">
  <h2>Unsere Filmauswahl</h2>
<div class="espai-filter">

</div>

  <ul class="grid">
    <?php foreach ($productions as $p) { ?>
      <li class="espai-presentation-list xl:col-3 lg:col-4 md:col-6 col-12">

        <a href="<?= trailingslashit(get_home_url()) ?>filme/<?= \app\espai\wordpress\EspaiPlugin::slugify($p->title) . '-' . $p->id ?>">
          <?php if (!empty($p->attachments['Poster'])) { ?>
            <span class="espai-poster">
              <img src="<?= trailingslashit($espai->baseUrl) ?>webservice/attachments/<?= $p->attachments['Poster'][0]->id ?>/datastream" alt="<?= esc_attr($p->attachments['Poster'][0]->caption) ?>" />
            </span>
	    <span class="espai-copyright"><?= $p->attachments['Poster'][0]->copyright ?></span>
          <?php } ?>
          <span class="espai-presentation-list-title"><?= $p->title ?></span>
        </a>
      </li>
    <?php } ?>
  </ul>
</div>

<?php get_footer(); ?>

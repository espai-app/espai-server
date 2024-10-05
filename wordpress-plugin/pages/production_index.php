<?php
$productions = \app\espai\wordpress\Production::list();
$spai_title = "Filmauswahl";
$espai = app\espai\wordpress\Espai::getInstance();

// collect all tags
/*$tagsToDisplay = ['Altersempfehlung', 'Themen', 'Unterrichtsfächer'];

$tags = [];
$selectedTags = [];
foreach ($tagsToDisplay as $t) {
  $tags[$t] = [];
  $selectedTags[$t] = isset($_GET[$t]) ? $_GET[$t] : [];
}

foreach ($productions as $production) {
  foreach ($production->tags as $type => $values) {
    if (!isset($tags[$type])) {
      continue;
    }

    $currentTags = $tags[$type];
    foreach ($values as $v) {
      if (!isset($currentTags[$v])) {
        $currentTags[$v] = [
            'title' => $v,
            'type' => $type,
            'selected' => in_array($v, $selectedTags[$type])];
      }
    }
    $tags[$type] = $currentTags;
  }
}

function sort_tags($t1, $t2) {
  if ($t1['selected'] && $t2['selected']
          || !$t1['selected'] && !$t2['selected']) {
    return strcasecmp($t1['title'], $t2['title']);
  }

  if ($t1['selected'] && !$t2['selected']) {
    return -1;
  }

  if (!$t1['selected'] && $t2['selected']) {
    return 1;
  }

  return 0;
}

$sortedTags = [];
foreach ($tags as $key => $values) {
  usort($values, 'sort_tags');
  $sortedTags[$key] = $values;
}

$filteredProductions = [];
foreach ($productions as $p) {
  $matches = true;
  foreach ($selectedTags as $type => $values) {
    if (empty($values) || !isset($p->tags[$type])) {
      continue;
    }

    $tag_matches = false;
    foreach ($p->tags[$type] as $pt) {
      if (in_array($pt, $values)) {
        $tag_matches = true;
        break;
      }
    }

    $matches &= $tag_matches;
  }

  if ($matches) {
    $filteredProductions[] = $p;
  }
}*/

$ageRangeStart = isset($_GET['recommendedAgeFrom']) ? (int)$_GET['recommendedAgeFrom'] : 0;
$ageRangeEnd = isset($_GET['recommendedAgeTo']) ? (int)$_GET['recommendedAgeTo'] : 99;

$filteredProductions = [];
foreach ($productions as $production) {
  if ($production->fromAge >= $ageRangeStart && $production->fromAge <= $ageRangeEnd) {
    $filteredProductions[] = $production;
  }
}

?>
<?php get_header(); ?>

<div class="espai-content">
  <h2>Unsere Filmauswahl</h2>

  <form method="GET" class="espai-filter">
    <!--<?php foreach ($sortedTags as $key => $values) { ?>
    <h3><?= esc_html($key) ?></h3>
    <ul>
      <?php foreach ($values as $v) { ?>
      <li>
        <input type="checkbox" name="<?= esc_attr($key) ?>[]" value="<?= esc_attr($v['title']) ?>" <?= $v['selected'] ? ' checked' : '' ?> />
        <?= $v['title'] ?>
      </li>
      <?php } ?>
    </ul>
    <?php } ?>-->

    <div class="espai-filter-age">
      filtern nach Altersstufe von <input type="number" name="recommendedAgeFrom" value="<?= isset($_GET['recommendedAgeFrom']) ? (int)$_GET['recommendedAgeFrom']: ''  ?>" /> bis <input type="number" name="recommendedAgeTo" value="<?= isset($_GET['recommendedAgeTo']) ? (int)$_GET['recommendedAgeTo']: ''  ?>" />

      <button type="submit">filtern</button>
    </div>
  </form>

  <ul class="grid">
    <?php foreach ($filteredProductions as $p) { ?>
      <li class="espai-presentation-list xl:col-3 lg:col-4 md:col-6 col-12">
        <a href="<?= trailingslashit(get_home_url()) ?>filme/<?= \app\espai\wordpress\EspaiPlugin::slugify($p->title) . '-' . $p->id ?>">
          <?php if (!empty($p->attachments['Poster'])) { ?>
            <img class="img-responsive" src="<?= trailingslashit($espai->baseUrl) ?>webservice/attachments/<?= $p->attachments['Poster'][0]->id ?>/datastream" alt="<?= esc_attr($p->attachments['Poster'][0]->caption) ?>" />
          <?php } ?>
          <span class="espai-presentation-list-title"><?= $p->title ?></span>
        </a>
      </li>
    <?php } ?>
  </ul>
</div>

<?php get_footer(); ?>
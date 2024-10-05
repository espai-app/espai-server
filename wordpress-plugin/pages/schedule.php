<?php

$date_parameter = get_query_var('espai_date');
if (empty($date_parameter)) {
  $date_parameter = 'auto';
}

$events = \app\espai\wordpress\Event::list();

$date = new DateTimeImmutable();
if ($date_parameter == 'auto') {
  if (!empty($events)) {
    $date = $events[0]->date;
  }
} else {
  $date = new DateTimeImmutable($date_parameter);
}
$one_day = DateInterval::createFromDateString('1 day');

$days_of_week = ['', 'Mo', 'Di', 'Mi', 'Do', 'Fr', 'Sa', 'So'];
$now = date('Y-m-d');
$program = [];

foreach ($events as $event) {
  if ($event->date < $now) {
    continue;
  }

  if (!isset($program[$event->date])) {
    $program[$event->date] = [];
  }

  $program[$event->date][] = $event;
}

?>

<?php get_header(); ?>

<div class="espai-content">
  <h2>
    Programm
  </h2>

  <?php foreach ($program as $key => $eventList) { ?>
    <?php $current_date = new \DateTimeImmutable($key); ?>
  <h3><?= $days_of_week[$current_date->format('N')] . ', ' . $current_date->format('d.m.Y') ?></h3>

      <table class="espai-schedule">
        <thead>
          <tr>
            <th style="width: 15%;">Uhrzeit</th>
            <th style="width: 45%;">Film</th>
            <th style="width: 40%;">Veranstaltungsort</th>
          </tr>
          <?php foreach ($eventList as $event) { ?>
          <tr>
            <td><?= substr($event->time, 0, 5) ?> Uhr</td>
            <td>
              <a href="<?= get_home_url() . '/filme/' . \app\espai\wordpress\EspaiPlugin::slugify($event->production->title) . '-' . $event->production->id . '?highlight=' . $event->id ?>">
                  <?= $event->production->title ?>
              </a>
            </td>
            <td><?= $event->venue->name ?></td>
          </tr>
          <?php } ?>
        </thead>

      </table>
    <?php } ?>
</div>

<?php get_footer();
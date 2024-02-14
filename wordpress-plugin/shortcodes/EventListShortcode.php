<?php

namespace app\espai\wordpress;

class EventListShortcode {

  public static function init() {
    add_shortcode('vhs_congres', array(get_called_class(), 'do_shortcode'));
  }

  public static function do_shortcode($args) {

    if (is_admin()) {
      return;
    }

    Event::listForEventSerial($args['serialId']);
    
    ob_start();
    self::render(eventList, true, true);
    return ob_get_clean();
  }

  public static function render($eventList, $showVenue = true, $showProduction = true) {
    
    if (empty($eventList)) { ?>
      <p class="empty">Im Moment finden keine Veranstaltungen statt.</p>
    <?php } else { ?>

      <table class="espai-table">
        <thead>
          <th>Datum</th>
          <?php if ($showProduction) { ?>
            <th>Film</th>
          <?php } ?>
          <?php if ($showVenue) { ?>
            <th>Kino</th>
          <?php } ?>
          <th>freie Plätze</th>
          <th></th>
        </thead>
        <tbody>
          <?php foreach ($eventList as $event) { ?>
            <tr>
              <td>
                <?= EspaiPlugin::formatDate($event->date) ?>, <?= EspaiPlugin::formatTime($event->time) ?> Uhr
              </td>
              <?php if ($showProduction) { ?>
                <td><?= $event->title ?> <?= !empty($event->version) ? '(' . $event->version . ')' : '' ?></td>
              <?php } ?>
              <?php if ($showVenue) { ?>
                <td><?= $event->venue->city ?>, <?= $event->venue->name ?></td>
              <?php } ?>
                <td><?= $event->availableTickets ?></td>
                <td>
                  <?php if ($event->availableTickets > 0) { ?>
                  <a href="<?= trailingslashit(get_home_url()) ?>buchen/veranstaltung/<?= EspaiPlugin::slugify($event->title) ?>-<?= $event->id ?>" rel="nofollow" class="button">
                      zur Buchung
                    </a>
                  <?php } else { ?>
                    ausgebucht
                  <?php } ?>
                </td>
              </tr>
          <?php } ?>
        </tbody>
      </table>
    <?php
    }
  }
}

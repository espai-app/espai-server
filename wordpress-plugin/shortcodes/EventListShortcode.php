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

  public static function render($eventList, $showVenue = true, $showProduction = true, $highlight = null) {

    if (empty($eventList)) { ?>
      <p class="empty">Im Moment finden keine Veranstaltungen statt.</p>
    <?php } else { ?>
          <?php $old_date = 'START'; ?>
          <?php foreach ($eventList as $event) { ?>
            <?php if ($old_date != $event->date) { ?>
              <?php if ($old_date != 'START') { ?>
                  </tbody>
                </table>
              <?php } ?>

              <?php $old_date = $event->date ?>

              <h4><?= EspaiPlugin::formatDate($event->date) ?></h4>
              <table class="espai-table espai-event-table">
                <thead>
                  <th>Uhrzeit</th>
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
            <?php } ?>
            <tr<?= (isset($highlight) && $event->id == $highlight) ? ' class="espai_highlighted"' : '' ?>>
              <td class="espai-event-table-time">
                <?= EspaiPlugin::formatTime($event->time) ?> Uhr
              </td>
              <?php if ($showProduction) { ?>
                <td class="espai-event-production">
                  <a href="<?= trailingslashit(get_home_url()) ?>filme/<?= \app\espai\wordpress\EspaiPlugin::slugify($event->title) . '-' . $event->production->id ?>?highlight=<?= $event->id ?>">
                    <?= $event->title ?> <?= !empty($event->version) ? '(' . $event->version . ')' : '' ?></td>
                  </a>
              <?php } ?>
              <?php if ($showVenue) { ?>
                <td class="espai-event-venue"><?= $event->venue->city ?>, <?= $event->venue->name ?></td>
              <?php } ?>
                <td class="espai-event-table-seats"><?= ($event->reservable && $event->availableTickets > 0) ? $event->availableTickets : '' ?></td>
                <td class="espai-event-table-reservation">
                  <?php if ($event->reservable && $event->availableTickets > 0) { ?>
                  <a href="<?= trailingslashit(get_home_url()) ?>buchen/veranstaltung/<?= EspaiPlugin::slugify($event->title) ?>-<?= $event->id ?>" rel="nofollow" class="button">
                      als Hort/Kita buchen
                    </a>
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

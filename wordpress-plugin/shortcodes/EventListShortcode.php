<?php

namespace app\espai\wordpress;

class EventListShortcode {

  public static function init() {
    add_shortcode('espai_events', array(get_called_class(), 'do_shortcode'));
  }

  public static function do_shortcode($args) {

    if (is_admin()) {
      return;
    }

    $espai = Espai::getInstance();
    $filter = [];

    if (isset($args['seasonid'])) {
      $filter['seasonId'] = $args['seasonid'];
    } else {
      $filter['seasonId'] = $espai->seasonId;
    }

    if (isset($args['venueid'])) {
      $filter['venueId'] = $args['venuid'];
    }

    if (isset($args['productionid'])) {
      $filter['productionId'] = $args['productionid'];
    }

    if (isset($args['serialid'])) {
      $filter['eventSerialId'] = $args['serialid'];
    }

    if (isset($args['productiontypeid'])) {
      $filter['productionTypeId'] = $args['productiontypeid'];
    }

    if (isset($args['date'])) {
      $filter['date'] = $args['date'];
    }

    if (isset($args['showpast'])) {
      $filter['showPast'] = $args['showpast'];
    }

    $eventList = Event::listFiltered($filter);

    ob_start();
    self::render($eventList, !isset($args['venueid']), !isset($args['productionid']));
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
                  <tr>
                    <th>Uhrzeit</th>
                    <?php if ($showProduction) { ?>
                      <th>Thema</th>
                    <?php } ?>
                    <?php if ($showVenue) { ?>
                      <th>Ort</th>
                    <?php } ?>
                    <th>freie Sitzplätze</th>
                    <th>freie Rollstuhlplätze</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
            <?php } ?>
            <tr<?= (isset($highlight) && $event->id == $highlight) ? ' class="espai_highlighted"' : '' ?>>
              <td class="espai-event-table-time">
                <?= EspaiPlugin::formatTime($event->time) ?> Uhr
              </td>
              <?php if ($showProduction) { ?>
                <td class="espai-event-production"<?= $showVenue ? ' data-title="Thema:"' : '' ?>>
                  <a href="<?= trailingslashit(get_home_url()) ?>filme/<?= \app\espai\wordpress\EspaiPlugin::slugify($event->title) . '-' . $event->production->id ?>?highlight=<?= $event->id ?>">
                    <?php if ($event->eventSerial !== null) { ?>
                      <span class="espai-event-serial"><?= $event->eventSerial->name ?></span>
                    <?php } ?>
                    <?= $event->title ?> <?= isset($event->version->versionName) && !empty($event->version->versionName)  ? '(' . $event->version->versionName . ')' : '' ?>
                  </a>
                  <?php if (!empty($event->childEvents)) { ?>
                    <ul>
                      <?php foreach ($event->childEvents as $c) { ?>
                        <li><?= $c->title ?></li>
                      <?php } ?>
                    </ul>
                  <?php } ?>
                </td>
              <?php } ?>
              <?php if ($showVenue) { ?>
                <td class="espai-event-venue"<?= $showProduction ? ' data-title="Ort:"' : '' ?>>
                  <a href="<?= get_home_url() . '/orte/' . $event->venue->slug ?>"><?= $event->venue->name ?></a>
                  <?php if (!$showProduction) { ?>
                    <?php if ($event->eventSerial !== null) { ?>
                      <span class="espai-event-serial"><?= $event->eventSerial->name ?></span>
                    <?php } ?>
                    <?php if (!empty($event->childEvents) && $event->childEvents[0]->production->id != 9001) { ?>
                      <ul>
                        <?php foreach ($event->childEvents as $c) { ?>
                          <li><?= $c->title ?></li>
                        <?php } ?>
                      </ul>
                    <?php } ?>
                  <?php } ?>
                </td>
              <?php } ?>

              <?php
                $seats_available = 0;
                if (isset($event->availableTickets->Sitzplatz)) {
                  $seats_available += $event->availableTickets->Sitzplatz;
                }
              ?>

                <td class="espai-event-table-seats" data-title="freie Sitzplätze:"><?= ($event->reservable && isset($event->availableTickets->Sitzplatz)) ? $event->availableTickets->Sitzplatz : '' ?></td>
                <td class="espai-event-table-seats" data-title="freie Rollstuhlplätze:"><?= ($event->reservable && isset($event->availableTickets->Rollstuhlplatz)) ? $event->availableTickets->Rollstuhlplatz : '' ?></td>
                <td class="espai-event-table-reservation">
                  <?php if ($event->reservable && $seats_available > 0) { ?>
                  <a href="<?= trailingslashit(get_home_url()) ?>buchen/veranstaltung/<?= EspaiPlugin::slugify($event->title) ?>-<?= $event->id ?>" rel="nofollow" class="button">
                      buchen
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

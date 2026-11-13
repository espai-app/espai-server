<?php
$reservation = $_SESSION['reservation'];
$espai_title = 'Adresse eingeben';

$reservationForm = \app\espai\wordpress\ReservationForm::getReservationForm($reservation);

$requiredFields = ['company', 'givenName', 'surname', 'address', 'postcode', 'city', 'email'];
$requiredExtras = [];

if ($reservationForm !== null && is_array($reservationForm->fields)) {
  foreach ($reservationForm->fields as $field ) {
    if ($field->required) {
      $requiredExtras[] = $field->name;
    }
  }
}
$errors = [];

if (isset($_POST['company'])) {

  $reservation->company = stripslashes($_POST['company']);
  $reservation->givenName = stripslashes($_POST['givenName']);
  $reservation->surname = stripslashes($_POST['surname']);
  $reservation->address = stripslashes($_POST['address']);
  $reservation->postcode = stripslashes($_POST['postcode']);
  $reservation->city = stripslashes($_POST['city']);
  $reservation->phone = stripslashes($_POST['phone']);
  $reservation->email = stripslashes($_POST['email']);
  $reservation->message = stripslashes($_POST['message']);
  $reservation->extras = $_POST['extras'];

  $_SESSION['reservation'] = $reservation;

  foreach ($requiredFields as $f) {
    if (empty($_POST[$f]) || trim($_POST[$f]) == '') {
      $errors[$f] = "Dies ist ein Pflichtfeld.";
    }
  }

  foreach ($requiredExtras as $f) {
    if (empty($_POST['extras'][$f]) || trim($_POST['extras'][$f]) =='') {
      $errors[$f] = "Dies ist ein Pflichtfeld.";
    }
  }

  if (empty($errors)) {
    header('Location: ' . trailingslashit(get_home_url()) . 'buchen/zusammenfassung');
    exit;
  }
}

?>

<?php get_header(); ?>
<div class="espai-content">
<form method="POST" class="espai-form">
  <h2>Adresse eingeben</h2>

  <div class="espai-fluid">
    <div class="field">
      <label for="company">Schule/Einrichtung*</label>
      <input type="text" name="company" id="company" value="<?= !empty($reservation->company) ? esc_attr($reservation->company) : '' ?>" maxlength="100" />
      <?php if (isset($errors['company'])) { ?><p class="validation-error"><?= $errors['company'] ?></p><?php } ?>
    </div>

    <div class="grid">
      <div class="field col-12 md:col-6">
        <label for="givenName">Vorname*</label>
        <input type="text" name="givenName" id="givenName" value="<?= !empty($reservation->givenName) ? esc_attr($reservation->givenName) : '' ?>" maxlength="100" />
        <?php if (isset($errors['givenName'])) { ?><p class="validation-error"><?= $errors['givenName'] ?></p><?php } ?>
      </div>

      <div class="field col-12 md:col-6">
        <label for="surname">Nachname*</label>
        <input type="text" name="surname" id="surname" value="<?= !empty($reservation->surname) ? esc_attr($reservation->surname) : '' ?>" maxlength="100" />
        <?php if (isset($errors['surname'])) { ?><p class="validation-error"><?= $errors['surname'] ?></p><?php } ?>
      </div>
    </div>

    <div class="field">
      <label for="address">Straße*</label>
      <input type="text" name="address" id="address" value="<?= !empty($reservation->address) ? esc_attr($reservation->address) : '' ?>" maxlength="100" />
      <?php if (isset($errors['address'])) { ?><p class="validation-error"><?= $errors['address'] ?></p><?php } ?>
    </div>

    <div class="grid">
      <div class="field col-4">
        <label for="postcode">PLZ*</label>
        <input type="text" name="postcode" id="postcode" value="<?= !empty($reservation->postcode) ? esc_attr($reservation->postcode) : '' ?>" maxlength="7" />
        <?php if (isset($errors['postcode'])) { ?><p class="validation-error"><?= $errors['postcode'] ?></p><?php } ?>
      </div>

      <div class="field col-8">
        <label for="city">Ort*</label>
        <input type="text" name="city" id="city" value="<?= !empty($reservation->city) ? esc_attr($reservation->city) : '' ?>" maxlength="100" />
        <?php if (isset($errors['city'])) { ?><p class="validation-error"><?= $errors['city'] ?></p><?php } ?>
      </div>
    </div>

    <div class="field">
      <label for="phone">Telefon</label>
      <input type="text" name="phone" id="phone" value="<?= !empty($reservation->phone) ? esc_attr($reservation->phone) : '' ?>" maxlength="50" />
      <?php if (isset($errors['phone'])) { ?><p class="validation-error"><?= $errors['phone'] ?></p><?php } ?>
    </div>

    <div class="field">
      <label for="email">E-Mail*</label>
      <input type="text" name="email" id="email" value="<?= !empty($reservation->email) ? esc_attr($reservation->email) : '' ?>" maxlength="100" />
      <?php if (isset($errors['email'])) { ?><p class="validation-error"><?= $errors['email'] ?></p><?php } ?>
      <p class="hint">Bitte geben Sie Ihre persönliche E-Mail-Adresse (nicht die allgemeine der Schule/Einrichtung) ein unter der wir Sie bei Rückfragen erreichen können.</p>
    </div>


    <div class="field">
      <label for="message">Nachricht an das Orga-Team</label>
      <textarea name="message" id="message" cols="50" rows="4"><?= !empty($reservation->message) ? esc_html($reservation->message) : '' ?></textarea>
      <?php if (isset($errors['message'])) { ?><p class="validation-error"><?= $errors['message'] ?></p><?php } ?>
    </div>

    <?php if ($reservationForm !== null && is_array($reservationForm->fields)) { ?>
      <h3>Statistische Angaben</h3>

      <?php foreach($reservationForm->fields as $field) { ?>

        <div class="field">
          <label for="<?= esc_attr($field->name) ?>"><?= esc_html($field->label) ?><?= $field->required ? '*' : '' ?></label>

          <?php if ($field->type == 'DROPDOWN') { ?>
            <select id="<?= esc_attr($field->name) ?>" name="extras[<?= esc_attr($field->name) ?>]">
              <option value="">-bitte auswählen-</option>
              <?php foreach($field->presets as $t) { ?>
                <option<?= isset($reservation->extras[$field->name]) && $reservation->extras[$field->name] == $t ? ' selected' : '' ?>><?= esc_html($t) ?></option>
              <?php } ?>
            </select>
          <?php } elseif ($field->type == 'MULTILINE') { ?>
            <textarea id="<?= esc_attr($field->name) ?>" name="extras[<?= esc_attr($field->name) ?>]"><?= isset($reservation->extras[$field->name]) ? esc_html($reservation->extras[$field->name]) : '' ?></textarea>
          <?php } else { ?>
            <input type="text" id="<?= esc_attr($field->name) ?>" name="extras[<?= esc_attr($field->name) ?>]" value="<?= isset($reservation->extras[$field->name]) ? esc_attr($reservation->extras[$field->name]) : '' ?>">
          <?php } ?>
          <?php if (isset($errors[$field->name])) { ?><p class="validation-error"><?= $errors[$field->name] ?></p><?php } ?>
        </div>
      <?php } ?>
    <?php } ?>
  </div>

  <div class="mt-5">
    <button type="submit">weiter ›</button>
  </div>
</form>
</div>

<script src="<?= trailingslashit(plugin_dir_url(ESPAI_PLUGIN)) ?>js/schulen_autocomplete.js"></script>
<script>autocomplete(document.getElementById("company"), "<?= ESPAI_BASE_URL ?>webservice/suggest/address");</script>
<?php get_footer(); ?>

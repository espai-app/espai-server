<?php
$reservation = $_SESSION['reservation'];
$espai_title = 'Adresse eingeben';

$schoolTypes = ['Grundschule', 'Oberschule', 'Gymnasium', 'Förderschule', 'Berufsschule', 'Sonstige'];
$level = [];
for ($i = 1; $i <= 13; $i++) {
  $level[] = $i . ". Klasse";
}

for ($i = 1; $i <= 3; $i++) {
  $level[] = $i . ". Lehrjahr";
}
$level[] = 'nicht zutreffend';

$visits = ['Das ist das erste Mal', '1 Mal', '2 Mal', '3 Mal', 'mehr als 3 Mal'];

$requiredFields = ['company', 'givenName', 'surname', 'address', 'postcode', 'city', 'email'];
$errors = [];

if (isset($_POST['company'])) {
  
  $reservation->company = $_POST['company'];
  $reservation->givenName = $_POST['givenName'];
  $reservation->surname = $_POST['surname'];
  $reservation->address = $_POST['address'];
  $reservation->postcode = $_POST['postcode'];
  $reservation->city = $_POST['city'];
  $reservation->phone = $_POST['phone'];
  $reservation->email = $_POST['email'];
  $reservation->message = $_POST['message'];
  $reservation->extras = $_POST['extras'];
  
  $_SESSION['reservation'] = $reservation;
  
  foreach ($requiredFields as $f) {
    if (empty($_POST[$f]) || trim($_POST[$f]) == '') {
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
  
  <div class="field">
    <label for="company">Schule*</label>
    <input type="text" name="company" id="company" value="<?= !empty($reservation->company) ? esc_attr($reservation->company) : '' ?>" maxlength="100" />
    <?php if (isset($errors['company'])) { ?><p class="validation-error"><?= $errors['company'] ?></p><?php } ?>
  </div>
  
  <div class="field">
    <label for="givenName">Vorname*</label>
    <input type="text" name="givenName" id="givenName" value="<?= !empty($reservation->givenName) ? esc_attr($reservation->givenName) : '' ?>" maxlength="100" />
    <?php if (isset($errors['givenName'])) { ?><p class="validation-error"><?= $errors['givenName'] ?></p><?php } ?>
  </div>
  
  <div class="field">
    <label for="surname">Nachname*</label>
    <input type="text" name="surname" id="surname" value="<?= !empty($reservation->surname) ? esc_attr($reservation->surname) : '' ?>" maxlength="100" />
    <?php if (isset($errors['surname'])) { ?><p class="validation-error"><?= $errors['surname'] ?></p><?php } ?>
  </div>
  
  <div class="field">
    <label for="address">Straße*</label>
    <input type="text" name="address" id="address" value="<?= !empty($reservation->address) ? esc_attr($reservation->address) : '' ?>" maxlength="100" />
    <?php if (isset($errors['address'])) { ?><p class="validation-error"><?= $errors['address'] ?></p><?php } ?>
  </div>
  
  <div class="field">
    <label for="postcode">PLZ*</label>
    <input type="text" name="postcode" id="postcode" value="<?= !empty($reservation->postcode) ? esc_attr($reservation->postcode) : '' ?>" maxlength="7" />
    <?php if (isset($errors['postcode'])) { ?><p class="validation-error"><?= $errors['postcode'] ?></p><?php } ?>
  </div>
  
  <div class="field">
    <label for="city">Ort*</label>
    <input type="text" name="city" id="city" value="<?= !empty($reservation->city) ? esc_attr($reservation->city) : '' ?>" maxlength="100" />
    <?php if (isset($errors['city'])) { ?><p class="validation-error"><?= $errors['city'] ?></p><?php } ?>
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
    <p class="hint">Bitte geben Sie Ihre persönliche E-Mail-Adresse (nicht die allgemeine der Schule) ein unter der wir Sie bei Rückfragen erreichen können.</p>
  </div>
  
  
  <div class="field">
    <label for="message">Nachricht an das SchulKinoWochen-Team</label>
    <textarea name="message" id="message" cols="50" rows="4"><?= !empty($reservation->message) ? esc_html($reservation->message) : '' ?></textarea>
    <?php if (isset($errors['message'])) { ?><p class="validation-error"><?= $errors['message'] ?></p><?php } ?>
  </div>
  
  <h3>Statistische Angaben</h3>
  
  <div class="field">
    <label for="schooltype">Schultyp</label>
    <select id="schooltype" name="extras[Schultyp]">
      <option value="">-bitte auswählen-</option>
      <?php foreach($schoolTypes as $t) { ?>
        <option<?= isset($reservation->extras['Schultyp']) && $reservation->extras['Schultyp'] == $t ? ' selected' : '' ?>><?= esc_html($t) ?></option>
      <?php } ?>
    </select>
  </div>
  
  <div class="field">
    <label for="level">Klasse</label>
    <select id="level" name="extras[Klassenstufe]">
      <option value="">-bitte auswählen-</option>
      <?php foreach($level as $t) { ?>
        <option<?= isset($reservation->extras['Klassenstufe']) && $reservation->extras['Klassenstufe'] == $t ? ' selected' : '' ?>><?= esc_html($t) ?></option>
      <?php } ?>
    </select>
  </div>
  
  <div class="field">
    <label for="subject">Im Rahmen welchen Fachs besuchen Sie die SchulKinoWochen?</label>
    <input type="text" name="extras[Unterrichtsfach]" id="subject" value="<?= !empty($reservation->extras['Unterrichtsfach']) ? esc_attr($reservation->extras['Unterrichtsfach']) : '' ?>" maxlength="100" />
  </div>
  
  <div class="field">
    <label for="visits">Wie oft waren Sie bereits bei den SchulKinoWochen?</label>
    <select id="visits" name="extras[Anz. Besuche]">
      <option value="">-bitte auswählen-</option>
      <?php foreach($visits as $t) { ?>
        <option<?= isset($reservation->extras['Anz. Besuche']) && $reservation->extras['Anz. Besuche'] == $t ? ' selected' : '' ?>><?= esc_html($t) ?></option>
      <?php } ?>
    </select>
  </div>
  
  <div>
    <button type="submit">weiter ›</button>
  </div>
</form>
</div>
<?php get_footer(); ?> 
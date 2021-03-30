<?php

require "db_connexion.php";

if (isset($_POST["idVisiteur"]) && isset($_POST["mois"]) && isset($_POST["km"]) && isset($_POST["repas"]) && isset($_POST["nuitee"]) && isset($_POST["etape"])) {

	// Id visiteur
	$idVisiteur = $_POST["idVisiteur"];

	// Frais Mois 
	$mois = $_POST["mois"];
	$km = $_POST["km"];
	$repas = $_POST["repas"];
	$nuitee = $_POST["nuitee"];
	$etape = $_POST["etape"]; 

	$response["mois"] = $mois;
	$response["km"] = $km;
	$response["repas"] = $repas;
	$response["nuitee"] = $nuitee;
	$response["etape"] = $etape;

	// insere une fiche de frais pour l'utilisteur connecté s'il n'en a pas déja une à la même date
	$sql_fichefrais_insert = "insert into fichefrais (idvisiteur, mois) values ('$idVisiteur', '$mois') on duplicate key update mois = '$mois'";

	mysqli_query($conn, $sql_fichefrais_insert);

	// insere une ligne etape dans lignefraisforfait
	$sql_lignefraisforfait_insert_etp = "insert into lignefraisforfait (idvisiteur, mois, idfraisforfait, quantite) values ('$idVisiteur', '$mois', 'ETP', '$etape')";

	mysqli_query($conn, $sql_lignefraisforfait_insert_etp);

	// insere une ligne km dans lignefraisforfait
	$sql_lignefraisforfait_insert_km = "insert into lignefraisforfait (idvisiteur, mois, idfraisforfait, quantite) values ('$idVisiteur', '$mois', 'KM', '$km')";

	mysqli_query($conn, $sql_lignefraisforfait_insert_km);

	// insere une ligne nuitee dans lignefraisforfait
	$sql_lignefraisforfait_insert_nui = "insert into lignefraisforfait (idvisiteur, mois, idfraisforfait, quantite) values ('$idVisiteur', '$mois', 'NUI', '$nuitee')";

	mysqli_query($conn, $sql_lignefraisforfait_insert_nui);

	// insere une ligne repas dans lignefraisforfait
	$sql_lignefraisforfait_insert_rep = "insert into lignefraisforfait (idvisiteur, mois, idfraisforfait, quantite) values ('$idVisiteur', '$mois', 'REP', '$repas')";

	mysqli_query($conn, $sql_lignefraisforfait_insert_rep);
		
	$response["messageFraisForfait"] = "Frais forfait effectué";

} else {
	$response["message"] = "Champs manquants";
}

echo json_encode($response);

$conn->close();

/*$response["nuitee"] = $nuitee;
$response["mois"] = $mois;
$response["km"] = $km;
$response["repas"] = $repas;
$response["etape"] = $etape;*/

/*$response["jour"] = $jour;
$response["montant"] = $montant;
$response["motif"] = $motif;*/
?>
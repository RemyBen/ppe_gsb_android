<?php

require "db_connexion.php";

// Id visiteur

$idVisiteur = $_POST["idVisiteur"];

// Frais Mois 
$mois = $_POST["mois"];
$km = $_POST["km"];
$repas = $_POST["repas"];
$nuitee = $_POST["nuitee"];
$etape = $_POST["etape"];

// Frais Hors Forfait
/*$jour = $_POST["jour"];
$montant = $_POST["montant"];
$motif = $_POST["motif"];*/

if (isset($idVisiteur) && isset($mois) && isset($km) && isset($repas) && isset($nuitee) && isset($etape)) {
	try {

		// insere une fiche de frais pour l'utilisteur connecté s'il n'en a pas déja une à la même date
		$sql_fichefrais_insert = "insert into fichefrais (idvisiteur, mois) values ('$idVisiteur', '$mois') on duplicate key update mois = '$mois'";

		$req_select = mysqli_query($conn, $sql_fichefrais_insert);
			
		$response["message"] = "Insert réussie";
	} catch {
		$response["message"] = "Une erreur est survenue";
	}

} else {
	$response["message"] = "Champs manquants";
}

$conn->close();

/*$response["nuitee"] = $nuitee;
$response["mois"] = $mois;
$response["annee"] = $annee;
$response["km"] = $km;
$response["repas"] = $repas;
$response["nuitee"] = $nuitee;
$response["etape"] = $etape;*/

/*$response["jour"] = $jour;
$response["montant"] = $montant;
$response["motif"] = $motif;*/
echo json_encode($response);
?>
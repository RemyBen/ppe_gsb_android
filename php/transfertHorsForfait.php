<?php

require "db_connexion.php";

if (isset($_POST["idVisiteur"]) && isset($_POST["mois"]) && isset($_POST["date"]) && isset($_POST["montant"]) && isset($_POST["motif"])) {

	// Id visiteur
	$idVisiteur = $_POST["idVisiteur"];
	
	$mois = $_POST["mois"];

	// Frais Hors Forfait
	$date = $_POST["date"];
	$montant = $_POST["montant"];
	$motif = $_POST["motif"];

	// insere une fiche de frais pour l'utilisteur connecté s'il n'en a pas déja une à la même date
	$sql_fichefrais_insert = "insert into fichefrais (idvisiteur, mois) values ('$idVisiteur', '$mois') on duplicate key update mois = '$mois'";

	// insere une ligne de frais hors forfait dans lignefraishorsforfait
	$sql_lignefraishorsforfait = "insert into lignefraishorsforfait (idvisiteur, mois, libelle, date, montant) select '$idVisiteur', '$mois', '$motif', '$date', '$montant' where not exists(select * from lignefraishorsforfait where idvisiteur = '$idVisiteur' and mois = '$mois' and libelle = '$motif' and date = '$date' and montant = '$montant')";

	mysqli_query($conn, $sql_lignefraishorsforfait);
		
	$response["messageFraisHorsForfait"] = "Hors forfait effectué";

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
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
/*$date = $_POST["date"];
$montant = $_POST["montant"];
$motif = $_POST["motif"];*/

if (isset($idVisiteur) && isset($mois) && isset($km) && isset($repas) && isset($nuitee) && isset($etape)) {
	
	try {
		// insere une fiche de frais pour l'utilisteur connecté s'il n'en a pas déja une à la même date
		$sql_fichefrais_insert = "insert into fichefrais (idvisiteur, mois) values ('$idVisiteur', '$mois') on duplicate key update mois = '$mois'";

		mysqli_query($conn, $sql_fichefrais_insert);

		$response["nuitee"] = $nuitee;
		$response["mois"] = $mois;
		$response["km"] = $km;
		$response["repas"] = $repas;
		$response["etape"] = $etape;

		/*$response["jour"] = $jour;
		$response["montant"] = $montant;
		$response["motif"] = $motif;*/

		// insere une ligne etape dans lignefraisforfait
		/*$sql_lignefraisforfait_insert_etp = "insert into lignefraisforfait (idvisiteur, mois, idfraisforfait, quantite) values ('$idVisiteur', '$mois', 'ETP', '$etape')";

		mysqli_query($conn, $sql_lignefraisforfait_insert_etp);

		// insere une ligne km dans lignefraisforfait
		$sql_lignefraisforfait_insert_km = "insert into lignefraisforfait (idvisiteur, mois, idfraisforfait, quantite) values ('$idVisiteur', '$mois', 'KM', '$km')";

		mysqli_query($conn, $sql_lignefraisforfait_insert_km);

		// insere une ligne nuitee dans lignefraisforfait
		$sql_lignefraisforfait_insert_nui = "insert into lignefraisforfait (idvisiteur, mois, idfraisforfait, quantite) values ('$idVisiteur', '$mois', 'NUI', '$nuitee')";

		mysqli_query($conn, $sql_lignefraisforfait_insert_nui);

		// insere une ligne repas dans lignefraisforfait
		$sql_lignefraisforfait_insert_rep = "insert into lignefraisforfait (idvisiteur, mois, idfraisforfait, quantite) values ('$idVisiteur', '$mois', 'REP', '$repas')";

		mysqli_query($conn, $sql_lignefraisforfait_insert_rep);*/

		// insere une ligne de frais hors forfait dans lignefraishorsforfait
		/*$sql_lignefraishorsforfait = "insert into lignefraishorsforfait (idvisiteur, mois, libelle, date, montant) select '$idVisiteur', '$mois', '$motif', '$date', '$montant' where not exists(select * from lignefraishorsforfait where idvisiteur = '$idVisiteur' and mois = '$mois' and libelle = '$motif' and date = '$date' and montant = '$montant')";
		
		mysqli_query($conn, $sql_lignefraishorsforfait);*/
			
		$response["message"] = "Opération effectuée";


	} catch(Exception $e){
		$response["message"] = "Erreur".$e->getMessage();
	}
	

} else {
	$response["message"] = "Champs manquants";
}

echo json_encode($response);

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
?>
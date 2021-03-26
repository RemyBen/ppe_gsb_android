<?php

require "db_connexion.php";

// log in
$username = $_POST["username"];
$password = $_POST["password"];

if (isset($username) && isset($password)) {

	$sql_login = "select * from visiteur where login = '$username' and mdp = '$password'";

	$req = mysqli_query($conn, $sql_login);

	if (mysqli_num_rows($req) > 0) {
		$response["succes"] = 1;
		$response["message"] = "Connexion réussie";

		// récupère l'id du visiteur correspondant aux identifiants
		$sql_getIdVisiteur = "select id from visiteur where login = '$username'";

		$req = mysqli_query($conn, $sql_getIdVisiteur);

		$row = mysqli_fetch_row($req);

		$response["idVisiteur"] = $row[0];
		
		echo json_encode($response);

	} else {
		$response["succes"] = 0;
		$response["message"] = "Connexion échouée";
		echo json_encode($response);
	}
} else {
	$response["succes"] = 0;
	$response["message"] = "Champs manquants";
	echo json_encode($response);
}
$conn->close();



?>
<?php

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "gsb_frais";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
 	echo "Connexion echouee: " . $conn->connect_error;
} else {
}

?>
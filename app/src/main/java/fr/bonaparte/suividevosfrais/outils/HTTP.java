package fr.bonaparte.suividevosfrais.outils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import fr.bonaparte.suividevosfrais.R;
import fr.bonaparte.suividevosfrais.models.FraisHf;
import fr.bonaparte.suividevosfrais.models.FraisMois;

public class HTTP extends AsyncTask<String, String, String> {

    private JSONParser jsonParser = new JSONParser();
    private AlertDialog.Builder builder;
    private AlertDialog chargement;
    private int success;
    private String message;

    Context context;

    public HTTP(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // crée un dialog
        builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setView(R.layout.dialog_chargement);
        chargement = builder.create();
        chargement.show();
    }

    @Override
    protected String doInBackground(String... parametres) {

        // initialise une collection "Clé, Valeur" qui sera envoyé au script login.php
        HashMap<String, String> loginParams = new HashMap<>();

        // paramètre username de l'EditText
        loginParams.put("username", parametres[0]);
        // parametre password de l'EditText
        loginParams.put("password", parametres[1]);

        // se connecter
        JSONObject login = jsonParser.makeHttpRequest("http://10.0.2.2/GSB_app/login.php", "POST", loginParams);

        try {
            // récupère le message de réussite ou non de la connexion (booleen)
            success = login.getInt("succes");
            message = login.getString("message");

            if (success == 1){
                // récupère l'id du visiteur
                String idVisiteur = login.getString("idVisiteur");

                // lis le fichier de données
                Hashtable hashtable = (Hashtable) Serializer.readSerialize(context);

                try {
                    for (Object key : hashtable.keySet()) {

                        // initialise une collection "Clé, Valeur" qui sera envoyé au script transfert.php
                        HashMap<String, String> transfertParams = new HashMap<>();

                        // récupère les frais du mois à la position "key" de la HashTable
                        FraisMois fraisMois = (FraisMois) hashtable.get(key);

                        // ajoute l'id du visiteur concerné à la collection transfertParams qui sera envoyé au script transfert.php
                        transfertParams.put("idVisiteur", idVisiteur);

                        //**** le mois dans la base est la concatenation de l'annee et du mois ex: 202002
                        String mois = String.valueOf(fraisMois.getMois());
                        if (mois.length() == 1){
                            mois = "0" + mois;
                        }
                        String annee = String.valueOf(fraisMois.getAnnee());

                        // besoin de la date format YYYY-MM-DD pour la lignefraishorforfait
                        String anneeMois = annee + "-" + mois; // donne un format YYYY-MM

                        // besoin du mois format YYYYMM comme évoqué précédemment pour fichefrais
                        mois = annee + mois;
                        //****

                        // ajoute le mois concerné à la collection transfertParams qui sera envoyé au script transfert.php
                        transfertParams.put("mois", mois);

                        String km = String.valueOf(fraisMois.getKm());
                        // ajoute les frais km à la collection transfertParams qui sera envoyé au script transfert.php
                        transfertParams.put("km", km);

                        String repas = String.valueOf(fraisMois.getRepas());
                        // ajoute les frais repas à la collection transfertParams qui sera envoyé au script transfert.php
                        transfertParams.put("repas", repas);

                        String nuitee = String.valueOf(fraisMois.getNuitee());
                        // ajoute les frais de nuitée à la collection transfertParams qui sera envoyé au script transfert.php
                        transfertParams.put("nuitee", nuitee);

                        String etape = String.valueOf(fraisMois.getEtape());
                        // ajoute les frais d'étape à la collection transfertParams qui sera envoyé au script transfert.php
                        transfertParams.put("etape", etape);

                        // récupère les frais hors forfait d'un fraisMois
                        ArrayList<FraisHf> fraisHfs = fraisMois.getLesFraisHf();

                        Log.d("TAG", "Frais forfait : " + "idVisiteur " + idVisiteur + " mois " + mois + " km " + km + " repas " + repas + " nuitee " + nuitee + " etape " + etape);

                        // transfert les frais forfaits
                        JSONObject transfert = jsonParser.makeHttpRequest("http://10.0.2.2/GSB_app/transfert.php", "POST", transfertParams);

                        for (int i = 0; i < fraisHfs.size(); i++) {

                            // initialise une collection "Clé, Valeur" qui sera envoyé au script transfertHorsForfait.php
                            HashMap<String, String> transfertParamsHorsForfait = new HashMap<>();
                            transfertParamsHorsForfait.put("idVisiteur", idVisiteur);
                            transfertParamsHorsForfait.put("mois", mois);

                            String jour = String.valueOf(fraisHfs.get(i).getJour());
                            // besoin de la date format YYYY-MM-DD pour la lignefraishorforfait
                            String date = anneeMois + "-" + jour;
                            // ajoute la date concerné à la collection transfertParams qui sera envoyé au script transfert.php
                            transfertParamsHorsForfait.put("date", date);

                            String montant = String.valueOf(fraisHfs.get(i).getMontant());
                            // ajoute le montant à la collection transfertParams qui sera envoyé au script transfert.php
                            transfertParamsHorsForfait.put("montant", montant);

                            String motif = String.valueOf(fraisHfs.get(i).getMotif());
                            // ajoute le motif à la collection transfertParams qui sera envoyé au script transfert.php
                            transfertParamsHorsForfait.put("motif", motif);

                            Log.d("TAG", "Frais Hors forfais : " + date + " " + mois);

                            // transfert les frais hors forfait
                            JSONObject transfert2 = jsonParser.makeHttpRequest("http://10.0.2.2/GSB_app/transfertHorsForfait.php", "POST", transfertParamsHorsForfait);

                        }
                    }
                } catch (Exception e){
                    // affiche le message
                    Toast.makeText(context, "Aucune donnée à transférer", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        chargement.dismiss();

        // si la connexion a réussi ou non, l'affiche dans un dialog
        if (success == 1) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setMessage(message);
            alert.setNeutralButton("ok", null);
            alert.show();
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setMessage(message);
            alert.setNeutralButton("ok", null);
            alert.show();
        }
    }
}

package fr.bonaparte.suividevosfrais.outils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

        builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setView(R.layout.dialog_chargement);
        chargement = builder.create();
        chargement.show();
    }

    @Override
    protected String doInBackground(String... parametres) {
        /*try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        HashMap<String, String> loginParams = new HashMap<>();

        loginParams.put("username", parametres[0]);
        loginParams.put("password", parametres[1]);

        /*hashMap.put("mois", String.valueOf(mois));
        hashMap.put("annee", String.valueOf(annee));
        hashMap.put("km", String.valueOf(km));
        hashMap.put("repas", String.valueOf(repas));
        hashMap.put("nuitee", String.valueOf(nuitee));
        hashMap.put("etape", String.valueOf(etape));
        hashMap.put("jour", String.valueOf(jour));
        hashMap.put("montant", String.valueOf(montant));
        hashMap.put("motif", String.valueOf(motif));*/

        JSONObject login = jsonParser.makeHttpRequest("http://10.0.2.2/GSB_app/login.php", "POST", loginParams);

        try {
            success = login.getInt("succes");
            message = login.getString("message");

            if (success == 1){
                String idVisiteur = login.getString("idVisiteur");


                Hashtable hashtable = (Hashtable) Serializer.readSerialize(context);

                /** TODO Mettre une valeur par défaut ? **/
                /*String mois = null;
                String annee = null;
                String km = null;
                String repas = null;
                String nuitee = null;
                String etape = null;
                //ArrayList<FraisHf> fraisHfs;
                String jour = null;
                String montant = null;
                String motif = null;*/

                for(Object key : hashtable.keySet()) {
                    HashMap<String, String> transfertParams = new HashMap<>();

                    FraisMois fraisMois = (FraisMois) hashtable.get(key);

                    transfertParams.put("idVisiteur", idVisiteur);

                    String mois = String.valueOf(fraisMois.getMois());

                    String annee = String.valueOf(fraisMois.getAnnee());
                    transfertParams.put("annee", annee);

                    String km = String.valueOf(fraisMois.getKm());
                    transfertParams.put("km", km);

                    String repas = String.valueOf(fraisMois.getRepas());
                    transfertParams.put("repas", repas);

                    String nuitee = String.valueOf(fraisMois.getNuitee());
                    transfertParams.put("nuitee", nuitee);

                    String etape = String.valueOf(fraisMois.getEtape());
                    transfertParams.put("etape", etape);

                    ArrayList<FraisHf> fraisHfs = fraisMois.getLesFraisHf();
                    for (int i = 0; i < fraisHfs.size(); i++) {
                        String jour = String.valueOf(fraisHfs.get(i).getJour());
                        String montant = String.valueOf(fraisHfs.get(i).getMontant());
                        String motif = String.valueOf(fraisHfs.get(i).getMotif());
                    }

                    JSONObject transfert = jsonParser.makeHttpRequest("http://10.0.2.2/GSB_app/transfert.php", "POST", transfertParams);

                    Log.d("TAG", "onPostExecute: " + "idVisiteur " + idVisiteur + " mois " + mois + " annee " + annee + " km " + km + " repas " + repas + " nuitee " + nuitee + " etape " + etape);
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

        if (success == 1){
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

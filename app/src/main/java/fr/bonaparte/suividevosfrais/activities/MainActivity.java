package fr.bonaparte.suividevosfrais.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Hashtable;
import java.util.Objects;

import fr.bonaparte.suividevosfrais.R;
import fr.bonaparte.suividevosfrais.activities.others.FraisEtapeActivity;
import fr.bonaparte.suividevosfrais.activities.others.FraisNuiteeActivity;
import fr.bonaparte.suividevosfrais.activities.others.FraisRepasActivity;
import fr.bonaparte.suividevosfrais.activities.others.HfActivity;
import fr.bonaparte.suividevosfrais.activities.others.HfRecapActivity;
import fr.bonaparte.suividevosfrais.activities.others.KmActivity;
import fr.bonaparte.suividevosfrais.models.FraisMois;
import fr.bonaparte.suividevosfrais.outils.Global;
import fr.bonaparte.suividevosfrais.outils.HTTP;
import fr.bonaparte.suividevosfrais.outils.Serializer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("GSB : Suivi des frais");
        // récupération des informations sérialisées
        recupSerialize();
        // chargement des méthodes événementielles
        cmdMenu_clic(((ImageButton) findViewById(R.id.cmdKm)), KmActivity.class);
        cmdMenu_clic(((ImageButton) findViewById(R.id.cmdRepas)), FraisRepasActivity.class);
        cmdMenu_clic(((ImageButton) findViewById(R.id.cmdNuitee)), FraisNuiteeActivity.class);
        cmdMenu_clic(((ImageButton) findViewById(R.id.cmdEtape)), FraisEtapeActivity.class);
        cmdMenu_clic(((ImageButton) findViewById(R.id.cmdHf)), HfActivity.class);
        cmdMenu_clic(((ImageButton) findViewById(R.id.cmdHfRecap)), HfRecapActivity.class);
        cmdTransfert_clic();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Récupère la sérialisation si elle existe
     */
    private void recupSerialize() {
        /* Pour éviter le warning "Unchecked cast from Object to Hash" produit par un casting direct :
         * Global.listFraisMois = (Hashtable<Integer, FraisMois>) Serializer.deSerialize(Global.filename, MainActivity.this);
         * On créé un Hashtable générique <?,?> dans lequel on récupère l'Object retourné par la méthode deSerialize, puis
         * on cast chaque valeur dans le type attendu.
         * Seulement ensuite on affecte cet Hastable à Global.listFraisMois.
        */
        Hashtable<?, ?> monHash = (Hashtable<?, ?>) Serializer.deSerialize(MainActivity.this);
        if (monHash != null) {
            Hashtable<Integer, FraisMois> monHashCast = new Hashtable<>();
            for (Hashtable.Entry<?, ?> entry : monHash.entrySet()) {
                monHashCast.put((Integer) entry.getKey(), (FraisMois) entry.getValue());
            }
            Global.listFraisMois = monHashCast;
        }
        // si rien n'a été récupéré, il faut créer la liste
        if (Global.listFraisMois == null) {
            Global.listFraisMois = new Hashtable<>();
            /* Retrait du type de l'HashTable (Optimisation Android Studio)
			 * Original : Typage explicit =
			 * Global.listFraisMois = new Hashtable<Integer, FraisMois>();
			*/

        }
    }

    /**
     * Sur la sélection d'un bouton dans l'activité principale ouverture de l'activité correspondante
     */
    private void cmdMenu_clic(ImageButton button, final Class classe) {
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // ouvre l'activité
                Intent intent = new Intent(MainActivity.this, classe);
                startActivity(intent);
            }
        });
    }


    /**
     * Cas particulier du bouton pour le transfert d'informations vers le serveur
     */
    private void cmdTransfert_clic() {
        findViewById(R.id.cmdTransfert).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                dialogConnexion();
            }
        });
    }

    /**
     * Crée un dialogue de connexion
     */
    private void dialogConnexion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(MainActivity.this));
        View nView = getLayoutInflater().inflate(R.layout.dialog_login, null);

        final TextInputLayout layout_identifiant = nView.findViewById(R.id.layout_identifiant);
        final TextInputLayout layout_mot_de_passe = nView.findViewById(R.id.layout_mot_de_passe);

        final EditText identifiant = nView.findViewById(R.id.identifiant);
        identifiant.requestFocus();

        final EditText mot_de_passe = nView.findViewById(R.id.mot_de_passe);

        identifiant.setOnFocusChangeListener((view, b) -> layout_identifiant.setError(null));

        mot_de_passe.setOnFocusChangeListener((view, b) -> layout_mot_de_passe.setError(null));

        builder.setPositiveButton(getString(R.string.dialog_connexion), (dialog, which) -> {

        });
        builder.setNegativeButton(getString(R.string.dialog_annuler), (dialog, which) -> {

            dialog.dismiss();
        });

        builder.setView(nView);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String unIdentifiant = identifiant.getText().toString();
                String unMotDePasse = mot_de_passe.getText().toString();
                if (!unIdentifiant.isEmpty() && !unMotDePasse.isEmpty()){
                    try {

                        //connecte l'utilisateur
                        connexion(unIdentifiant, unMotDePasse);

                        dialog.dismiss();

                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (unIdentifiant.isEmpty() && unMotDePasse.isEmpty()){
                        layout_identifiant.setError(getString(R.string.dialog_erreur_champs));
                        layout_mot_de_passe.setError(getString(R.string.dialog_erreur_champs));
                    } else if (unIdentifiant.isEmpty()){
                        layout_identifiant.setError(getString(R.string.dialog_erreur_champs));
                        layout_mot_de_passe.setError(null);
                    } else if(unMotDePasse.isEmpty()){
                        layout_mot_de_passe.setError(getString(R.string.dialog_erreur_champs));
                        layout_identifiant.setError(null);
                    }
                }
            }
        });
    }

    /**
     * Ouvre une URL avec en parametre
     * @param unIdentifiant l'identifiant de connexion
     * @param unMotDePasse le mot de passe de connexion
     */
    private void connexion(String unIdentifiant, String unMotDePasse) {
        String[] parametres = new String[2];
        parametres[0] = unIdentifiant;
        parametres[1] = unMotDePasse;

        HTTP http = new HTTP(MainActivity.this);
        http.execute(parametres);
    }
}

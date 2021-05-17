package fr.bonaparte.suividevosfrais.activities.others;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import fr.bonaparte.suividevosfrais.R;
import fr.bonaparte.suividevosfrais.activities.MainActivity;
import fr.bonaparte.suividevosfrais.models.FraisMois;
import fr.bonaparte.suividevosfrais.outils.Global;
import fr.bonaparte.suividevosfrais.outils.Serializer;

public class HfActivity extends AppCompatActivity {


	private DatePicker uneDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hf);
        setTitle("GSB : Frais HF");
        // modification de l'affichage du DatePicker
        Global.changeAfficheDate((DatePicker) findViewById(R.id.datHf), true) ;
		// mise à 0 du montant
		((EditText)findViewById(R.id.txtHf)).setText("0") ;

		// active les boutons que pour l'annee précédente
		disableButton();

		// chargement des méthodes événementielles
		imgReturn_clic() ;
		cmdAjouter_clic() ;
	}

	private void disableButton() {

		// récupère le mois et l'année actuelle
		Calendar calendar = Calendar.getInstance();
		int currentMonth = calendar.get(Calendar.MONTH) +1;
		int currentYear = calendar.get(Calendar.YEAR);

		uneDate = findViewById(R.id.datHf);
		uneDate.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

				Integer annee = ((DatePicker)findViewById(R.id.datHf)).getYear() ;
				Integer mois = ((DatePicker)findViewById(R.id.datHf)).getMonth() + 1 ;

				// Active la modification d'un frais hors forfait que si la date séelectionné est comprise entre le moins actuel et 1 an et 1 mois en arriere.
				// Soit les frais saisis peuvent remonter jusqu’à un an en arrière
				if ((annee == currentYear && mois <= currentMonth) || (annee == currentYear-1 && mois >= currentMonth -1)) {
					findViewById(R.id.txtHf).setEnabled(true);
					findViewById(R.id.txtHfMotif).setEnabled(true);
					findViewById(R.id.cmdHfAjouter).setEnabled(true);
				} else {
					findViewById(R.id.txtHf).setEnabled(false);
					findViewById(R.id.txtHfMotif).setEnabled(false);
					findViewById(R.id.cmdHfAjouter).setEnabled(false);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_actions, menu);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(getString(R.string.retour_accueil))) {
            retourActivityPrincipale() ;
        }
        return super.onOptionsItemSelected(item);
    }

	/**
	 * Sur la selection de l'image : retour au menu principal
	 */
    private void imgReturn_clic() {
    	findViewById(R.id.imgHfReturn).setOnClickListener(new ImageView.OnClickListener() {
    		public void onClick(View v) {
    			retourActivityPrincipale() ;    		
    		}
    	}) ;
    }

    /**
     * Sur le clic du bouton ajouter : enregistrement dans la liste et sérialisation
     */
    private void cmdAjouter_clic() {
    	findViewById(R.id.cmdHfAjouter).setOnClickListener(new Button.OnClickListener() {
    		public void onClick(View v) {
    			enregListe() ;
    			Serializer.serialize(Global.listFraisMois, HfActivity.this) ;
    			retourActivityPrincipale() ;    		
    		}
    	}) ;    	
    }
    
	/**
	 * Enregistrement dans la liste du nouveau frais hors forfait
	 */
	private void enregListe() {
		// récupération des informations saisies
		Integer annee = ((DatePicker)findViewById(R.id.datHf)).getYear() ;
		Integer mois = ((DatePicker)findViewById(R.id.datHf)).getMonth() + 1 ;
		Integer jour = ((DatePicker)findViewById(R.id.datHf)).getDayOfMonth() ;
		Float montant = Float.valueOf((((EditText)findViewById(R.id.txtHf)).getText().toString()));
		String motif = ((EditText)findViewById(R.id.txtHfMotif)).getText().toString() ;
		// enregistrement dans la liste
		Integer key = annee*100+mois ;
		if (!Global.listFraisMois.containsKey(key)) {
			// creation du mois et de l'annee s'ils n'existent pas déjà
			Global.listFraisMois.put(key, new FraisMois(annee, mois)) ;
		}
		Global.listFraisMois.get(key).addFraisHf(montant, motif, jour) ;
	}

	/**
	 * Retour à l'activité principale (le menu)
	 */
	private void retourActivityPrincipale() {
		Intent intent = new Intent(HfActivity.this, MainActivity.class) ;
		startActivity(intent) ;   					
	}
}

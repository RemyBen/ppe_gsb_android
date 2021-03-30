package fr.bonaparte.suividevosfrais.activities.others;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

import fr.bonaparte.suividevosfrais.R;
import fr.bonaparte.suividevosfrais.activities.MainActivity;
import fr.bonaparte.suividevosfrais.models.FraisMois;
import fr.bonaparte.suividevosfrais.outils.Global;
import fr.bonaparte.suividevosfrais.outils.Serializer;

public class FraisEtapeActivity extends AppCompatActivity {

	// informations affichées dans l'activité
	private Integer annee ;
	private Integer mois ;
	private Integer qte ;

	private DatePicker uneDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frais_etape);
        setTitle("GSB : Frais d'étapes");
		// modification de l'affichage du DatePicker
		Global.changeAfficheDate((DatePicker) findViewById(R.id.dateEtape), false) ;
		uneDate = (DatePicker) findViewById(R.id.dateEtape);
		// valorisation des propriétés
		valoriseProprietes() ;
        // chargement des méthodes événementielles
		imgReturn_clic() ;
		cmdValider_clic() ;
		cmdPlus_clic() ;
		cmdMoins_clic() ;
		dat_clic() ;
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
	 * Valorisation des propriétés avec les informations affichées
	 */
	private void valoriseProprietes() {
		annee = ((DatePicker)findViewById(R.id.dateEtape)).getYear() ;
		mois = ((DatePicker)findViewById(R.id.dateEtape)).getMonth() + 1 ;

		// récupère le mois et l'année actuelle
		Calendar calendar = Calendar.getInstance();
		int currentMonth = calendar.get(Calendar.MONTH);
		int currentYear = calendar.get(Calendar.YEAR);

		// désactive les boutons pour les mois différents du mois actuel
		if (mois == currentMonth+1 && annee == currentYear){
			findViewById(R.id.cmdEtapePlus).setEnabled(true);
			findViewById(R.id.cmdEtapeMoins).setEnabled(true);
			findViewById(R.id.cmdEtapeValider).setEnabled(true);
		} else {
			findViewById(R.id.cmdEtapePlus).setEnabled(false);
			findViewById(R.id.cmdEtapeMoins).setEnabled(false);
			findViewById(R.id.cmdEtapeValider).setEnabled(false);
		}

		// récupération de la qte correspondant au mois actuel
		qte = 0 ;
		Integer key = annee*100+mois ;
		if (Global.listFraisMois.containsKey(key)) {
			qte = Global.listFraisMois.get(key).getEtape() ;
		}
		((TextView)findViewById(R.id.txtEtape)).setText(String.format(Locale.FRANCE, "%d", qte)) ;
	}
	
	/**
	 * Sur la selection de l'image : retour au menu principal
	 */
    private void imgReturn_clic() {
    	findViewById(R.id.imgEtapeReturn).setOnClickListener(new ImageView.OnClickListener() {
    		public void onClick(View v) {
    			retourActivityPrincipale() ;    		
    		}
    	}) ;
    }

    /**
     * Sur le clic du bouton valider : sérialisation
     */
    private void cmdValider_clic() {
    	findViewById(R.id.cmdEtapeValider).setOnClickListener(new Button.OnClickListener() {
    		public void onClick(View v) {
    			Serializer.serialize(Global.listFraisMois, FraisEtapeActivity.this) ;
    			retourActivityPrincipale() ;    		
    		}
    	}) ;    	
    }
    
    /**
     * Sur le clic du bouton plus : ajout de 10 dans la quantité
     */
    private void cmdPlus_clic() {
    	findViewById(R.id.cmdEtapePlus).setOnClickListener(new Button.OnClickListener() {
    		public void onClick(View v) {
    			qte+=10 ;
    			enregNewQte() ;
    		}
    	}) ;    	
    }
    
    /**
     * Sur le clic du bouton moins : enlève 10 dans la quantité si c'est possible
     */
    private void cmdMoins_clic() {
    	findViewById(R.id.cmdEtapeMoins).setOnClickListener(new Button.OnClickListener() {
    		public void onClick(View v) {
   				qte = Math.max(0, qte-10) ; // suppression de 10 si possible
    			enregNewQte() ;
     		}
    	}) ;    	
    }
    
    /**
     * Sur le changement de date : mise à jour de l'affichage de la qte
     */
    private void dat_clic() {
    	uneDate.init(uneDate.getYear(), uneDate.getMonth(), uneDate.getDayOfMonth(), new OnDateChangedListener(){
			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				valoriseProprietes() ;				
			}
    	});       	
    }

	/**
	 * Enregistrement dans la zone de texte et dans la liste de la nouvelle qte, à la date choisie
	 */
	private void enregNewQte() {
		// enregistrement dans la zone de texte
		((TextView)findViewById(R.id.txtEtape)).setText(String.format(Locale.FRANCE, "%d", qte)) ;
		// enregistrement dans la liste
		Integer key = annee*100+mois ;
		if (!Global.listFraisMois.containsKey(key)) {
			// creation du mois et de l'annee s'ils n'existent pas déjà
			Global.listFraisMois.put(key, new FraisMois(annee, mois)) ;
		}
		Global.listFraisMois.get(key).setEtape(qte);
	}

	/**
	 * Retour à l'activité principale (le menu)
	 */
	private void retourActivityPrincipale() {
		Intent intent = new Intent(FraisEtapeActivity.this, MainActivity.class) ;
		startActivity(intent) ;   					
	}
}

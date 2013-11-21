package com.example.jeuxlabo;

import com.example.jeuxlabo.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context; 
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas; 
import android.graphics.Point; 
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable; 
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View; 
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BounceView extends RelativeLayout { 
	// Activity parent
	Activity mParent;
	Context mContext;
	
	// boolean si la biere tombe
	private boolean mIsMoving = false;
	
	// Elements Graphique de la View
	ImageView mBeer;  		// Object repr�sentant la biere 
	ImageView mTray;  		// Object repr�sentant le plateau
	ImageView mTrayShadow;  // Object repr�sentant l'ombre du plateau
	TextView mTextView;  	// Objet repr�sentant le d�compte et le start;
	 
    //  taille deu plateau (pixels)
	private int mTraySize;
	
	// taille de la bi�re (pixels)
	private int mBeerSize;
	
	// Centre du plateau 
	private int  mTrayCenterX = 0;
	private int mTrayCenterY = 0;
	
	// position initial de l'ombre du plateau (tray), initialis�e dans le constructeur
	private int mTrayShadowInitX = 0;
	private int mTrayShadowInitY = 0;
	
	// d�placement maximun de la beer, initialis� dans le constructeur
	private int mBeerMaxDistance = 0;
	
	// Angle du plateau
	private double mX_Angle = 0;
	private double mZ_Angle = 0;
		 
	
	// Intervalle de temps pour la mise � jour de la position de la biere (en millisecondes), ici 30 FPS
	private final int UPDATE_INTERVAL = 1000/30 ; 
	
	private int mCountDown = 3;
	// jeu d'animation pour la s�quence de texte du d�part
	private AnimationSet mStartAnimSet;
	 
	// Runnable qui met � jour la positon de la biere � intervalle r�guli�re (UPDATE_INTERVAL) 
	private Runnable mUpdateRunnable =  new Runnable() { 
	      
			@SuppressLint("NewApi")
			@Override 
	        public void run()  
	        { 
	        	if(mIsMoving){
	        		// initialisation des param�tres pour la premi�re m�j
	        		if(mTrayCenterX == 0){
	        			  mTrayCenterX = (int) (mTray.getX() + mTraySize / 2) ;
	        		      mTrayCenterY = (int) (mTray.getY() + mTraySize / 2) ;
	        		      
	        		      mTrayShadowInitX = (int) mTrayShadow.getX();	        		      
	        		      mTrayShadowInitY = (int)  mTrayShadow.getY();
	        		      
	        		}
	        		
	        		// mise � jour de la position de la biere
	        		mBeer.setY((float) (mBeer.getY() - mX_Angle*0.8));
	        		mBeer.setX((float) (mBeer.getX() + mZ_Angle*0.8));        			        		
	        	
	        		// m�j de l'�chelle de la biere (effet de  profondeur lorsque la biere s'�loigne du centre
	        		float newBeerScale =  (float) ( 1 - ((getBeerDistanceFromTrayCenter() / mBeerMaxDistance)*0.3)) ;
	        		mBeer.setScaleX(newBeerScale);
	        		mBeer.setScaleY(newBeerScale);
	        		
	        		// m�j de l'ombre du plateau
	        		mTrayShadow.setY((float) ((float) (mTrayShadowInitY) + mX_Angle*5));
	        		mTrayShadow.setX((float) ((float) (mTrayShadowInitX) - mZ_Angle*5));
	        			        		
	        		// mise � jour de l'orientation du plateau
	        		mTray.setRotationX((float) mX_Angle);
	        		mTray.setRotationY((float) mZ_Angle);
	        		
	        		// gestion de la chute de la biere ou pas	        		
		        	if(getBeerDistanceFromTrayCenter() > mBeerMaxDistance){
		        		
		        			// on arr�tes la m�j du plateau et de la bi�re
		        			stopMoving();
		        			
		        			// Animation de la chute de la biere
		        			mTray.bringToFront();
		        			mBeer.animate().scaleX(0).scaleY(0).setDuration(300).withEndAction(new Runnable(){

								@Override
								public void run() {
									
					 				// � la fin de la chute, on change l'image par l'image du verre cass� avec la flaque
					 				mBeer.setScaleX(2);
					 				mBeer.setScaleY(2);
					 				mBeer.setImageResource(R.drawable.broken_beer);
									
								}});
		        			
		        			
		        			
		        			// on joue le son du verre cass�
		        			MediaPlayer mp = MediaPlayer.create(mContext, R.raw.sound);
		                    mp.setOnCompletionListener(new OnCompletionListener() { 	 
		                        @Override 
		                        public void onCompletion(MediaPlayer mp) {
		                            // TODO Auto-generated method stub 
		                            mp.release();
		                        } 		 
		                    });    
		                    mp.start();
		                    
		                    // si le compte � rebour est fini, on affiche le texte 
		                    if(mStartAnimSet.hasEnded()){
		                    	mTextView.setText("RETRY?");
		                    	mTextView.setVisibility(View.VISIBLE);
		                    }
		        	}
		        	else{
		        		// on post la mise � jour d� la View
		        		BounceView.this.postDelayed(this, UPDATE_INTERVAL);
		        	}
		        	
	        	}
	        } 
	 };


	 
	 // Variables li�es au deplacement de mBeer         
     public BounceView(Context context) { 
    	 super(context); 
         mParent = (Activity) context;
         mContext = context;
          
          // Loading Activity View
         LayoutInflater.from(context).inflate(R.layout.bounce_view, this);
         //  Creationd du background de la View en mode REPEAT
         BitmapDrawable background = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.carreaux));

         background.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
         this.setBackgroundDrawable(background);
         
         
          // Loading beer Object
         mBeer = (ImageView) findViewById(R.id.beer);          
          // Loading  tray Object
         mTray = (ImageView) findViewById(R.id.tray);
         // Loading  tray shadow Object
         mTrayShadow = (ImageView) findViewById(R.id.tray_shadow);
         
         // Loading Text Object
         mTextView = (TextView)findViewById(R.id.text);
          
         // calcul des tailles en pixels
         mTraySize = dipToPixels(180);
         mBeerSize = dipToPixels(60);        
         
         
         // Definition du pivot de rotation du plateau (le milieu)         
         mTray.setPivotX(mTraySize/2);
         mTray.setPivotY(mTraySize/2);	 
       
         // Distance maximum avant que la biere tombe
         mBeerMaxDistance = mTraySize/2;
        
        
        
         
         
         
         // Jeux d'animations pour le compte � rebour
         mStartAnimSet = new AnimationSet(true);
         ScaleAnimation scaleAnim = new ScaleAnimation(1, 10, 1, 10, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,1.0f );
         AlphaAnimation alphaAnim = new AlphaAnimation(1, 0);
         
         alphaAnim.setDuration(1000);
         scaleAnim.setDuration(1000);
         alphaAnim.setFillAfter(false);
         alphaAnim.setFillAfter(false);
         
         mStartAnimSet.addAnimation(scaleAnim);
         mStartAnimSet.addAnimation(alphaAnim);
         mStartAnimSet.setRepeatCount(0);
         mStartAnimSet.setFillAfter(false);
         mStartAnimSet.getAnimations().get(1).setAnimationListener(new AnimationListener(){
        	
			@Override
			public void onAnimationEnd(Animation animation) { // Listener d�clanher lorsque l'animation se termine
				
				mCountDown = mCountDown - 1;
				
				if(mCountDown <  0){
					if(mIsMoving){
						mTextView.setVisibility(View.GONE);
					}
					else{ // cas on perd alors que l'animation de "GO" n'est pas fini
						mTextView.setText("RETRY?");
					}
				}
				else if(mCountDown == 0){// on d�marre le jeu
					mTextView.setText("GO!");
					mTextView .startAnimation(mStartAnimSet);
					startMoving();
				}
				else{// on continue le compte � rebour
					mTextView.setText(String.valueOf(mCountDown));		
					mTextView.startAnimation(mStartAnimSet);
				}				
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}});
         
         mTextView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(mTextView.getText().equals("RETRY?")){
					// R�initialisaton									
					mBeer.setImageResource(R.drawable.biere);
					mBeer.bringToFront();
					mBeer.invalidate();
					// position de la bi�re
					mBeer.setX(mTrayCenterX - mBeerSize/2);
					mBeer.setY(mTrayCenterY - mBeerSize/2);
					
					// �chelle de la bi�re
					mBeer.setScaleX(1);
	        		mBeer.setScaleY(1);
	        		
	        		mTextView.bringToFront();
	        		
	        		// position de l'ombre du plateau
	        		mTrayShadow.setX(mTrayShadowInitX);
	        		mTrayShadow.setY(mTrayShadowInitY);
	        		
	        		// Angle du plateau
	        		mTray.setRotationX((float) 0);
	        		mTray.setRotationY((float) 0);
	        		
					startGame();
				}
					
				
			}});
         
         startGame(); 
        	 
     }
     // D�marage du Jeux (compte � rebour, puis on commencer � "bouger"
     public void startGame(){  
    	 mCountDown = 3;
    	 mTextView.setText("3");
	     mTextView.startAnimation(mStartAnimSet);
     }
       
         

     // d�marage du mouvement
     public void startMoving(){
    	 mIsMoving = true;
    	this.post(mUpdateRunnable);
     }
     
    //arr�t du movement
     public void stopMoving(){
    	 mIsMoving = false;
     }
     

     // Fonction qui met � jour l'orientation du plateau (mTray)
     public void setTrayAngle(double x, double z){
    	 mX_Angle = x;
    	 mZ_Angle = z;
     }
     
     // http://fr.wikipedia.org/wiki/Distance_entre_deux_points_sur_le_plan_cart%C3%A9sien
     private float getBeerDistanceFromTrayCenter(){
    	 return (float) Math.sqrt(Math.pow(getBeerCenterX() - mTrayCenterX,2) + Math.pow(getBeerCenterY() - mTrayCenterY,2));
     }
   
     // accesseur pour le X du centre du plateau
     public int getBeerCenterX(){   	    	
    	 return  (int) (mBeer.getX() + (mBeerSize / 2));
    	
     }
     
     // accesseur pour le Y du centre du plateau
     public int getBeerCenterY(){
    	 return  (int) (mBeer.getY() + (mBeerSize / 2));
     }
     
  // Convertion de l'unit� "dip" density independant pixel en pixel, selon les caracct�ristiques de l'�cran
     public int dipToPixels(float dipValue) {
		    DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
		    return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
     }

}
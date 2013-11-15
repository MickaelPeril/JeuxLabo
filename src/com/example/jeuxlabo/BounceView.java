package com.example.jeuxlabo;

import com.example.jeuxlabo.R;

import android.content.Context; 
import android.graphics.Canvas; 
import android.graphics.Point; 
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable; 
import android.view.View; 
import android.widget.Button;
import android.widget.ImageView;

public class BounceView extends View { 
	
	
	// boolean si la biere depasse le cercle
		protected boolean depassement=false;
		//sprite du cercle rouge
		protected Drawable mySpriteCircle;
		
     /* Our Ball together with the location it will be painted*/ 
     protected Drawable mySprite; 
	
     //protected Point mySpritePos = new Point(0,0); 
     protected Point mySpritePos = new Point(150,200); 
      
     /* Working with a Enum is 10000% 
      * safer than working with int's 
      * to 'remember' the direction. */ 
     static float mRoll, mPitch, mHeading;
     protected enum HorizontalDirection {LEFT, RIGHT} 
     protected enum VerticalDirection {UP, DOWN} 
     protected HorizontalDirection myXDirection = HorizontalDirection.RIGHT; 
     protected VerticalDirection myYDirection = VerticalDirection.UP; 


     public BounceView(Context context) { 
          super(context); 
          // Set the background 
          this.setBackgroundResource( R.drawable.table_campus );
          // Load our "Ball" 
          this.mySprite = this.getResources().getDrawable(R.drawable.biere);
          this.mySpriteCircle = this.getResources().getDrawable(R.drawable.cercle);
     } 

     @Override 
     protected void onDraw(Canvas canvas) { 
          /* Check if the Ball started to leave 
           * the screen on left or right side */ 
    	 
    	 if(!depassement){ //si la biere ne sort pas du cercle
    		 /* Check if the Ball started to leave 
              * the screen on left or right side */ 
	         this.mySpritePos.x -= mPitch; 
	         
	          if (mySpritePos.x >= this.getWidth() - mySprite.getBounds().width()) { 
	              mySpritePos.x = this.getWidth() - mySprite.getBounds().width();
	               //this.myXDirection = HorizontalDirection.LEFT; 
	          } else if (mySpritePos.x <= 0) { 
	              mySpritePos.x = 0;
	               //this.myXDirection = HorizontalDirection.RIGHT; 
	          } 
	          
	         
	         this.mySpritePos.y -= mHeading; 
	          
	          if (mySpritePos.y >= this.getHeight() - mySprite.getBounds().height()) { 
	              mySpritePos.y = this.getHeight() - mySprite.getBounds().height(); 
	         } else if (mySpritePos.y <= 0) { 
	             mySpritePos.y = 0; 
	         } 

    	 }

          /* Set the location, where the sprite 
           * will draw itself to the canvas */ 
    	 this.mySpriteCircle.setBounds(50, 150, 450,450+100); 
          this.mySprite.setBounds(this.mySpritePos.x, this.mySpritePos.y, 
                    this.mySpritePos.x + 250, this.mySpritePos.y + 250); 
           
          /* Make the sprite draw itself to the canvas */ 
          this.mySpriteCircle.draw(canvas);
          this.mySprite.draw(canvas); 
          
          //collision/depassement
          if(this.mySpritePos.x < 50 || this.mySpritePos.y < 150 ||   this.mySpritePos.x + 250 > 450 || this.mySpritePos.y + 250 > 550 ){
        	 fonctionDepassement();
          }
     } 
     
   //fonction de depassement
     public void  fonctionDepassement(){
    	 depassement=true;
     }
}
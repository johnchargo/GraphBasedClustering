package undirectedGraphDisplay;

import java.awt.Color;

/*
 * Class used to calculate the color to use for weighted edges.
 * Heavier weighted edges are given a stronger color.
 */

public class ColorGradient {
	private static Color [] colors;
	ColorGradient()
	{
		colors = new Color[10];
          
		  Color c1 = Color.blue;
          for (int i = 0; i < colors.length; i++) {
            int alpha = (int)(255 * (float)i / (float)colors.length);
            Color c = new Color(c1.getRed(), c1.getGreen(),
                                c1.getBlue(), alpha);
            colors[i] = c;        
        }
	};
	
	public static Color getColor(int colorChoice, int minColor, int maxColor)
	{
		if(colorChoice >= maxColor)
		{
			colorChoice = maxColor -1;
		}
		
		if((maxColor-minColor > 0))
		{
			double adjustedInput =(((double)colors.length)/(maxColor -minColor))*(colorChoice-minColor);
			return colors[(int)adjustedInput];
		}
		else
		{
			return Color.black;
		}
	}
}

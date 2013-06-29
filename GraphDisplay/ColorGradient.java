package GraphDisplay;

import java.awt.Color;

public class ColorGradient {
	private static Color [] colors;
	ColorGradient()
	{
		colors = new Color[10];
		/*  commented out code to do
		Color c1 = Color.yellow;
        Color c2 = Color.blue;
        for (int i = 0; i < colors.length; i++) {
          float ratio = (float)i / (float)colors.length;
          int red = (int)(c2.getRed() * ratio + c1.getRed() * (1 - ratio));
          int green = (int)(c2.getGreen() * ratio +
                            c1.getGreen() * (1 - ratio));
          int blue = (int)(c2.getBlue() * ratio +
                           c1.getBlue() * (1 - ratio));
          Color c = new Color(red, green, blue);
          colors[i] = c;
          */
          
		  Color c1 = Color.blue;
          for (int i = 0; i < colors.length; i++) {
            int alpha = (int)(255 * (float)i / (float)colors.length);
            Color c = new Color(c1.getRed(), c1.getGreen(),
                                c1.getBlue(), alpha);
            colors[i] = c;        
        }
	};
	
	public static Color getColor(double colorChoice, double minColor, double maxColor)
	{
		if(colorChoice >= maxColor)
		{
			colorChoice = maxColor -1;
		}
		
		if((maxColor-minColor > 0))
		{
			double adjustedInput =(((double)colors.length)/(maxColor -minColor))*(colorChoice-minColor);
			//System.out.println("adjusted input is: " + adjustedInput);
			return colors[(int)adjustedInput];
		}
		else
		{
			return Color.black;
		}
	}
}

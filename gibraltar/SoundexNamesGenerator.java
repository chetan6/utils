package gibraltar;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.language.Soundex;
import org.apache.commons.codec.net.*;

public class SoundexNamesGenerator {
	
	public static void main(String [] a) {
		String test = "GALINDO, Gilmer Antonio";
		Soundex soundEx = new Soundex();
		System.out.println(soundEx.encode(test) + ";"+ soundEx.soundex(test));
		
		QCodec bc = new QCodec();
		try {
			System.out.println(bc.decode(soundEx.encode(test)));
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

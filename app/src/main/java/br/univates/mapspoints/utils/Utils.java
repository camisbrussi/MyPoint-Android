package br.univates.mapspoints.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.WindowManager;
import android.widget.EditText;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Utils {
	private static Utils instancia = new Utils();
	public Dialog dialog;

	// GPS:
	public static final int REQUEST_GPS_ACCESS = 6;

	// CARTOES
	public static final String numCartaoPrefix = "**** **** **** ";
	public static final String VISA_STRING = "VISA";
	public static final String MASTER_STRING = "MASTER";
	public static final String AMEX_STRING = "AMEX";
//	public static final String DISCOVER_STRING = "";// nao usada

	// tipoPagamento
	public static final int DINHEIRO = 1;
	public static final int BOLETO = 4;
	public static final int CREDITO = 5;
	public static final int DEBITO = 6;
	public static final int SALDO = 10;

	private Utils() {
	}

	public static Utils getInstance() {
		return (instancia);
	}

	public int[] splitData(String data) {
		String[] splitData = data.split(" ");
		String[] splitDia  = splitData[0].split("-");
		String[] splitHora = splitData[1].split(":");

		int[] dataSplit = new int[6];
		dataSplit[0] = Integer.parseInt(splitDia[0]);
		dataSplit[1] = Integer.parseInt(splitDia[1]);
		dataSplit[2] = Integer.parseInt(splitDia[2]);
		dataSplit[3] = Integer.parseInt(splitHora[0]);
		dataSplit[4] = Integer.parseInt(splitHora[1]);
		dataSplit[5] = Integer.parseInt(splitHora[2]);

		System.out.println(dataSplit.toString());
		return dataSplit;
	}

	public int[] splitDataValidadeCartao(String data) {
		String[] split = data.split("/");

		int[] mesAno = {0,0};
		mesAno[0] = Integer.parseInt(split[0]);
		mesAno[1] = Integer.parseInt(split[1]);

		System.out.println(mesAno[0] + " / " + mesAno[1]);
		return mesAno;
	}

	public int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	public float getPerc2PxAXIS(final Context c, float perc, char axis) {
		float res = 0;
		char eaxis = 'x';
		float auxx = 0f;
		float auxy = 0f;
		float cont = 0;
		while (cont != 2) {
			cont++;
			if (eaxis == 'x') {
				Display display = ((WindowManager) c.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				int x = size.x;
				float dp = (x * perc) / 100;
				auxx = dp;
				res = dp;
				eaxis = 'y';
			} else if (eaxis == 'y') {
				Display display = ((WindowManager) c.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				int y = size.y;
				float dp = (y * perc) / 100;
				auxy = dp;
				res = dp;
				eaxis = 'x';
			} else if (axis == 'z') {
			}
		}

		if (auxx > auxy) {
			float auxiliar = auxx;
			auxx = auxy;
			auxy = auxiliar;
		}

		if (axis == 'x') {
			res = auxx;
		} else if (axis == 'y') {
			res = auxy;
		}
		return res;
	}


	public void alerta(Context c, String title, String msg) {
		new AlertDialog.Builder(c).setTitle(title).setMessage(msg).setPositiveButton("OK", null).show();
	}

	public String convertDataToString(String data) {
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		try {
			date = formato.parse(data);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		String result = "";
		if (data != null) {
			SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy");
			result = out.format(date);
		}
		return result;
	}

	public int obterDiferencaDeHoras(String horaSaida) {
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		long diff = 0;

		try {
			Date dataAtual = new Date();
			Date dataSaida = myFormat.parse(horaSaida);
//			diff = date2.getTime() - date1.getTime();
          if (dataAtual.compareTo(dataSaida) == 1) {
				diff = dataAtual.getTime() - dataSaida.getTime();
			} else {
				diff = dataSaida.getTime() - dataAtual.getTime();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (int) TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS);
	}

	public int obterDiferencaDeHoras(String horaChegada, String horaSaida) {
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		long diff = 0;

		try {
			Date dataChegada = myFormat.parse(horaChegada);
			Date dataSaida = myFormat.parse(horaSaida);

			if (dataChegada.compareTo(dataSaida) == 1) {
				diff = dataChegada.getTime() - dataSaida.getTime();
			} else {
				diff = dataSaida.getTime() - dataChegada.getTime();
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		System.out.println("DIFERENCA >>>>>>>>>>>>>>>>>>>>>> " + TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS));
		return (int) TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS);
	}


	public String convertDataHoraToStringSemCaracteEspecial(Date data) {
		if (data != null) {
			SimpleDateFormat out = new SimpleDateFormat("yyyyMMddHHmmss");
			String result = out.format(data);
			return result;
		} else {
			return null;
		}
	}

	public void criaLog(String text) {
		File logFile = new File("log.file");
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			// BufferedWriter for performance, true to set append to file flag
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
			buf.append(text);
			buf.newLine();
			buf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String utf8Decode(String ss) {
		String s;
		try {
			s = URLDecoder.decode(ss, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			s = ss;
		}
		return s;
	}

	public static String maskBoletoString(String codigoBoleto) {
		try {
			return String.format("%s.%s\n%s.%s\n%s.%s %s\n%s",
					codigoBoleto.substring(0,4),
					codigoBoleto.substring(5,9),
					codigoBoleto.substring(10,14),
					codigoBoleto.substring(15,20),
					codigoBoleto.substring(21,25),
					codigoBoleto.substring(26,31),
					codigoBoleto.charAt(32),
					codigoBoleto.substring(33,46));
		} catch (Exception  e) {
			e.printStackTrace();
			return null;
		}
	}

	public abstract static class MaskEditUtil {

		public static final String FORMAT_CPF = "###.###.###-##";
		public static final String FORMAT_FONE = "(###)####-#####";
		public static final String FORMAT_CEP = "#####-###";
		public static final String FORMAT_DATE = "##/##/####";
		public static final String FORMAT_HOUR = "##:##";

		public static TextWatcher mask(final EditText ediTxt, final String mask) {
			return new TextWatcher() {
				boolean isUpdating;
				String old = "";

				@Override
				public void afterTextChanged(final Editable s) {}

				@Override
				public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {}

				@Override
				public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
					final String str = MaskEditUtil.unmask(s.toString());
					String mascara = "";
					if (isUpdating) {
						old = str;
						isUpdating = false;
						return;
					}
					int i = 0;
					for (final char m : mask.toCharArray()) {
						if (m != '#' && str.length() > old.length()) {
							mascara += m;
							continue;
						}
						try {
							mascara += str.charAt(i);
						} catch (final Exception e) {
							break;
						}
						i++;
					}
					isUpdating = true;
					ediTxt.setText(mascara);
					ediTxt.setSelection(mascara.length());
				}
			};
		}

		public static String unmask(final String s) {
			return s.replaceAll("[.]", "").replaceAll("[-]", "").replaceAll("[/]", "").replaceAll("[(]", "").replaceAll("[ ]","").replaceAll("[:]", "").replaceAll("[)]", "");
		}
	}
}


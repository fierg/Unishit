package utils;

public class Utilities {
	public static boolean contains(String[] arr, String key) {
		for (String string : arr) {
			if (key.equals(string)) {
				return true;
			}
		}
		return false;
	}
	
	public static String buildStaticTex() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("\\documentclass[10pt, a4paper]{article}\n");
		sb.append("\\usepackage[utf8]{inputenc}\n");
		sb.append("\\usepackage[margin=1in]{geometry}\n");
		sb.append("\\begin{document}\n");
		sb.append("\\section*{Output}\n\n");
		
		return sb.toString();
	}
	public static void main(String[] args) {
		System.out.println(buildStaticTex());
	}

}

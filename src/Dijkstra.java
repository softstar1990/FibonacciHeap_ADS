public class Dijkstra {

	public static void main(String[] args) {
		
	      if ( args.length < 1 || args[0].charAt(0) != '-' ) {
	          usage();
	          return;
	       }
	      
	      switch ( args[0].charAt(1) ) {
	         case 'r': 
	        	 	   if (args.length != 4) {
						usage();
						return;
					   }
	        	 	   int n = Integer.parseInt(args[1]);
	        	 	   double d = Double.parseDouble(args[2]);
	        	 	   int x = Integer.parseInt(args[3]);
	        	       Algorithm.randomInput(n,d,x);
	                   break;
	         case 's': 
	        	 	   if(args.length != 2){
	        	 		   usage();
	        	 		   return;
	        	 	   }
	        	 	   Algorithm.userSimple(args[1]);
	                   break;
	         case 'f': 
		      	 	   if(args.length != 2){
		    	 		   usage();
		    	 		   return;
		    	 	   }
		    	 	   Algorithm.userFheap(args[1]);
		               break;
	         default:  usage();
	                   return;
	      }
	}
	
	static void usage() {
		System.out.println("usage:   Dijkstra <option>");
		System.out.println("options: \t -r n d x \t random");
		System.out.println("options: \t -s file_name \t user input simple");
		System.out.println("options: \t -f file_name \t user input f-heap");
	}	

}

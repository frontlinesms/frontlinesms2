package frontlinesms2

class CustomActivity extends Activity {
	List steps
	
	static String getShortName() { 'customactivity' }
	static hasMany = [steps: Step]
}

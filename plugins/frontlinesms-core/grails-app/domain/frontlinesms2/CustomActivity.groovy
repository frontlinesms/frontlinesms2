package frontlinesms2

class CustomActivity extends Activity {
	static String getShortName() { 'customactivity' }

	static hasMany = [steps: Step]
	List steps
}

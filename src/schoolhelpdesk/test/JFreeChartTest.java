package schoolhelpdesk.test;

// JFreeChart imports - temporarily commented out to allow compilation
// import org.jfree.chart.ChartFactory;
// import org.jfree.chart.ChartPanel;
// import org.jfree.chart.JFreeChart;
// import org.jfree.data.general.DefaultPieDataset;

/**
 * Simple test to verify JFreeChart imports are working
 */
public class JFreeChartTest {
    
    public static void main(String[] args) {
        System.out.println("Testing JFreeChart imports...");
        
        try {
            // JFreeChart temporarily disabled - uncomment when dependencies are loaded
            System.out.println("JFreeChart imports are temporarily commented out.");
            System.out.println("Please refresh Maven dependencies in NetBeans and uncomment imports.");
            
            /*
            // Test creating a simple dataset
            DefaultPieDataset dataset = new DefaultPieDataset();
            dataset.setValue("Test", 100);
            
            // Test creating a chart
            JFreeChart chart = ChartFactory.createPieChart(
                "Test Chart", dataset, true, true, false);
            
            // Test creating a chart panel
            ChartPanel panel = new ChartPanel(chart);
            
            System.out.println("SUCCESS: All JFreeChart imports are working!");
            System.out.println("Chart created: " + (chart != null));
            System.out.println("Chart panel created: " + (panel != null));
            */
            
        } catch (Exception e) {
            System.out.println("ERROR: JFreeChart imports failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

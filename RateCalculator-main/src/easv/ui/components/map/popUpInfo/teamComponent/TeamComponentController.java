package easv.ui.components.map.popUpInfo.teamComponent;
import easv.be.Employee;
import easv.be.Team;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

import javax.tools.Tool;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class TeamComponentController  implements Initializable {
    @FXML
    private VBox teamComponent;

    @FXML
    private Label totalOverheadHourly;
    @FXML
    private Label totalOverheadDaily;
    @FXML
    private Label salaryOverhead;
    @FXML
    private PieChart teamChart;
    private Team team;
    private ObservableList<PieChart.Data> pieChartData;

    public TeamComponentController(Team team) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TeamComponent.fxml"));
        loader.setController(this);
         this.team=team;
         pieChartData= FXCollections.observableArrayList();
        try {
     loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializePieChart();
        Tooltip totalOverhed=  new Tooltip();
        totalOverhed.setText(totalOverheadDaily.getText());
        totalOverheadDaily.setTooltip(totalOverhed);
        Tooltip hourOverhead = new Tooltip();
        hourOverhead.setText(totalOverheadHourly.getText());
        totalOverheadHourly.setTooltip(hourOverhead);
    }

    public VBox getRoot() {
        return teamComponent;
    }


/**initialize chart with the values*/
        private void initializePieChart(){

            if(team==null){
                initializeTeamNull();
                return;
            }

            if(team.getEmployees()==null){
                initializeTeamNoEmployees();
                return;
            }

            for(Employee employee :team.getEmployees()){

                String employeeData = employee.getName() +" " + employee.getActiveConfiguration().getDayRate() + " " + employee.getCurrency();
                pieChartData.add(new PieChart.Data(employeeData,employee.getActiveConfiguration().getDayRate().doubleValue()));
            }
            teamChart.setData(pieChartData);
            teamChart.setTitle(team.getTeamName());
            teamChart.setLabelLineLength(10);
            teamChart.setLegendVisible(false);
            for (PieChart.Data data : pieChartData) {
                data.getNode().setOnMouseClicked(event -> {
                    data.setPieValue(data.getPieValue());
                });
            }
            initializeTeamOverheadData();
        }

        /**initialize the team ovearhead values
         * */

        private void initializeTeamOverheadData(){
        if(team.getActiveConfiguration()!=null){
            BigDecimal totalOverheadDayValue =   team.getActiveConfiguration().getTeamDayRate();
            BigDecimal totalOverheadHourValue = team.getActiveConfiguration().getTeamHourlyRate();
            totalOverheadDaily.setText(totalOverheadDayValue.setScale(2,RoundingMode.HALF_UP) + " " + team.getCurrency());
             totalOverheadHourly.setText(totalOverheadHourValue.setScale(2,RoundingMode.HALF_UP)+ " " + team.getCurrency());
        }else{
            totalOverheadDaily.setText("n/a");
            totalOverheadHourly.setText("n/a");
        }
        }

        // show that the team has no data
       private void initializeTeamNoEmployees(){
          totalOverheadDaily.setText("No available data for the team " + team.getTeamName());
          totalOverheadHourly.setText("No availabel date for the team " +  team.getTeamName()) ;
       }
    private void initializeTeamNull(){
        totalOverheadDaily.setText("No available data for the team " );
        totalOverheadHourly.setText("No availabel date for the team ") ;
    }


}

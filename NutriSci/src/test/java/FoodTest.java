//import org.Dao.FoodNameDAO;
//import org.Entity.FoodName;
//import org.Handlers.Database.DatabaseFoodNameDAO;
//
//import java.sql.SQLException;
//import java.util.List;
//
//public class FoodTest {
//    public static void main(String[] args) throws SQLException {
//        FoodNameDAO foodDao = new DatabaseFoodNameDAO();
//        List<FoodName> foods = foodDao.getAllFoodNames();
//
//        if (foods.isEmpty()) {
//            System.out.println("no food found.");
//        }
//        else {
//            System.out.println("food found.");
//
//            for (FoodName food : foods) {
//                System.out.println("food ID: " + food.getFoodId() + ", description: " + food.getFoodDescription());
//            }
//        }
//    }
//}

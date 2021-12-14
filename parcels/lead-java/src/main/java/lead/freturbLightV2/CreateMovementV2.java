package lead.freturbLightV2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateMovementV2 {

    static Map<String, MovementFunction> movementFunctionMap = createMovementFunctionMap();

    static void calculateMovementsWithCorrection(List<FirmDataV2> firms) throws Exception {
        for (FirmDataV2 firm : firms) {
            int count = 0;
            double movments = 0;
            for (CategorisationV2.St45Class st45Class : CategorisationV2.st45Map.values()) {
                if (firm.st8 == st45Class.st8) {
                    movments += selectCorrectFunction(movementFunctionMap.get(st45Class.st45), firm.employees);
                    count++;
                }
            }
            if (count != 0) {
                firm.unrealMovements = (movments / count);
//                firm.movements = (int) Math.round(movments / (count));
            }
            if (firm.st8 == 2) {
                firm.movements = (int) Math.round(firm.unrealMovements * 0.381882926);
            } else if (firm.st8 == 3) {
                firm.movements = (int) Math.round(firm.unrealMovements * 0.415974427);
            } else if (firm.st8 == 4) {
                firm.movements = (int) Math.round(firm.unrealMovements * 0.689677865);
            } else if (firm.st8 == 6) {
                firm.movements = (int) Math.round(firm.unrealMovements * 0.718453224);
            } else if (firm.st8 == 7) {
                firm.movements = (int) Math.round(firm.unrealMovements * 0.443875807);
            }
        }
    }

    static void calculateMovements(List<FirmDataV2> firms) throws Exception {
        for (FirmDataV2 firm : firms) {
            int count = 0;
            double movments = 0;
            for (CategorisationV2.St45Class st45Class : CategorisationV2.st45Map.values()) {
                if (firm.st8 == st45Class.st8) {
                    movments += selectCorrectFunction(movementFunctionMap.get(st45Class.st45), firm.employees);
                    count++;
                }
            }
            if (count != 0) {
                firm.movements = (int) Math.round(movments / (count));
            }
        }
    }

    private static double selectCorrectFunction(MovementFunction movementFunction, int employees) throws Exception {
        if (movementFunction.type.equals("RATIO_Constant_LAET")) {
            return movementFunction.x * employees;
        } else if (movementFunction.type.equals("FUNCTION_LIN")) {
            return movementFunction.x * employees + movementFunction.y;
        } else if (movementFunction.type.equals("FUNCTION_LOG")) {
            return movementFunction.x * Math.log(employees) + movementFunction.y;
        } else if (movementFunction.type.equals("FUNCTION_LOG_NO_CONS")) {
            return movementFunction.x * Math.log(employees);
        } else {
            throw new Exception("no function type for this establishment");
        }
    }

    private static Map<String, MovementFunction> createMovementFunctionMap() {
        Map<String, MovementFunction> movementFunctionMap = new HashMap<>();
        movementFunctionMap.put("1", new MovementFunction("FUNCTION_LOG_NO_CONS",2.396,0));
        movementFunctionMap.put("2-2", new MovementFunction("FUNCTION_LOG",1.933,14.307));
        movementFunctionMap.put("2-3", new MovementFunction("FUNCTION_LIN",3.132,-2.2));
        movementFunctionMap.put("2-4", new MovementFunction("FUNCTION_LIN",0.789,2.492));
        movementFunctionMap.put("26Ha", new MovementFunction("FUNCTION_LOG",6.416,0.195));
        movementFunctionMap.put("26Mi", new MovementFunction("FUNCTION_LIN",0.101,1.628));
        movementFunctionMap.put("26Mo", new MovementFunction("FUNCTION_LIN",0.14,5.059));
        movementFunctionMap.put("3", new MovementFunction("FUNCTION_LIN",1.536,3.077));
        movementFunctionMap.put("4-2", new MovementFunction("FUNCTION_LIN",1.172,6.893));
        movementFunctionMap.put("5-2", new MovementFunction("FUNCTION_LOG",1.045,13.342));
        movementFunctionMap.put("5-4", new MovementFunction("FUNCTION_LOG_NO_CONS",6.246,0));
        movementFunctionMap.put("5-5", new MovementFunction("FUNCTION_LIN",1.8,0.719));
        movementFunctionMap.put("4-6", new MovementFunction("RATIO_Constant_LAET",2.59,0));
        movementFunctionMap.put("4-7", new MovementFunction("FUNCTION_LIN",0.176,8.75));
        movementFunctionMap.put("34-2", new MovementFunction("FUNCTION_LIN",0.614,19.720));
        movementFunctionMap.put("34-3", new MovementFunction("FUNCTION_LIN",0.352,7.574));
        movementFunctionMap.put("7-2", new MovementFunction("FUNCTION_LOG",15.086,0.026));
        movementFunctionMap.put("8-2", new MovementFunction("RATIO_Constant_LAET",2.57,0));
        movementFunctionMap.put("9-2", new MovementFunction("RATIO_Constant_LAET",7.62,0));
        movementFunctionMap.put("7-3", new MovementFunction("RATIO_Constant_LAET",2.3,0));
        movementFunctionMap.put("8-3", new MovementFunction("RATIO_Constant_LAET",2.52,0));
        movementFunctionMap.put("9-3", new MovementFunction("FUNCTION_LOG",19.31,10.01));
        movementFunctionMap.put("10", new MovementFunction("FUNCTION_LIN",0.108,79.785));
        movementFunctionMap.put("11", new MovementFunction("FUNCTION_LIN",0.961,0.793));
        movementFunctionMap.put("12", new MovementFunction("FUNCTION_LOG_NO_CONS",10.626,0));
        movementFunctionMap.put("13", new MovementFunction("RATIO_Constant_LAET",1.57,0));
        movementFunctionMap.put("14", new MovementFunction("FUNCTION_LOG_NO_CONS",3.029,0));
        movementFunctionMap.put("15", new MovementFunction("FUNCTION_LOG",5.124,3.362));
        movementFunctionMap.put("16", new MovementFunction("RATIO_Constant_LAET",1.55,0));
        movementFunctionMap.put("17", new MovementFunction("FUNCTION_LOG",1.057,5.364));
        movementFunctionMap.put("18", new MovementFunction("FUNCTION_LIN",0.329,4.277));
        movementFunctionMap.put("19", new MovementFunction("FUNCTION_LOG",4.437,12.43));
        movementFunctionMap.put("20", new MovementFunction("FUNCTION_LOG",0.107,3.3347));
        movementFunctionMap.put("21", new MovementFunction("FUNCTION_LIN",1.413,0.685));
        movementFunctionMap.put("22", new MovementFunction("FUNCTION_LOG",4.998,16.764));
        movementFunctionMap.put("23", new MovementFunction("FUNCTION_LOG",3.304,2.748));
        movementFunctionMap.put("29", new MovementFunction("FUNCTION_LOG",-0.681,5.015));
        movementFunctionMap.put("6", new MovementFunction("FUNCTION_LOG",0.795,1.053));
        movementFunctionMap.put("25", new MovementFunction("FUNCTION_LIN",0.074,1.801));
        movementFunctionMap.put("27-2", new MovementFunction("RATIO_Constant_LAET",0.64,0));
        movementFunctionMap.put("27-3", new MovementFunction("FUNCTION_LOG_NO_CONS",4.657,0));
        movementFunctionMap.put("26Fa", new MovementFunction("FUNCTION_LIN",0.157,1.941));
        movementFunctionMap.put("30", new MovementFunction("RATIO_Constant_LAET",12.12,0));
        movementFunctionMap.put("28-2", new MovementFunction("RATIO_Constant_LAET",4.37,0));
        movementFunctionMap.put("28-3", new MovementFunction("FUNCTION_LIN",4.841,9.429));
        return movementFunctionMap;
    }

    static class MovementFunction {

        String type;
        double x;
        double y;

        MovementFunction(String type, double x, double y) {
            this.type = type;
            this.x = x;
            this.y = y;
        }

    }
}

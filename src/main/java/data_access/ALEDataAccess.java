package data_access;

import use_case.label.ALEDataAccessInterface;
import use_case.label.AddLabelExpense;
import entity.Label;

import java.util.HashMap;
import java.util.Map;

public class ALEDataAccess implements ALEDataAccessInterface {

    private Map<Integer, AddLabelExpense> ale =  new HashMap<>();

    public ALEDataAccess() {
        ale.put(1, new AddLabelExpense(1, 30.00, true));
        ale.put(2, new AddLabelExpense(2, 100.00, false));
    }

    @Override
    public AddLabelExpense getAddLabelExpense(int id) {
        return ale.get(id);
    }

    @Override
    public void removeLabelFromAllEntries(int labelId) {
        for(AddLabelExpense e : ale.values()) {
            e.getLabel().removeIf(l -> l.getLabelId() == labelId);
        }


    }

    @Override
    public void assignLabelExpense(int id, Label label) {
        AddLabelExpense addLabelExpense = ale.get(id);
        if (addLabelExpense != null) {
            addLabelExpense.AddExpense(label);
        }

    }
}

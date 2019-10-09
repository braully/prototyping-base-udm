package com.github.braully.app;

import com.github.braully.domain.Account;
import com.github.braully.domain.AccountTransaction;
import com.github.braully.domain.Address;
import com.github.braully.domain.BinaryFile;
import com.github.braully.domain.Budget;
import com.github.braully.domain.City;
import com.github.braully.domain.Contact;
import com.github.braully.domain.Email;
import com.github.braully.domain.FinancialAccount;
import com.github.braully.domain.GenericType;
import com.github.braully.domain.GenericValue;
import com.github.braully.domain.InfoExtra;
import com.github.braully.domain.Inventory;
import com.github.braully.domain.Organization;
import com.github.braully.domain.Partner;
import com.github.braully.domain.Phone;
import com.github.braully.domain.Product;
import com.github.braully.domain.PurchaseOrder;
import com.github.braully.domain.PurchaseOrderItem;
import com.github.braully.domain.Task;
import com.github.braully.domain.UserMessage;
import com.github.braully.domain.util.BilletTicket;
import com.github.braully.domain.util.BilletTicketAgreement;
import com.github.braully.domain.util.EntityDummy;
import com.github.braully.domain.util.FinancialCharge;
import com.github.braully.web.DescriptorExposedEntity;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author braully
 */
public class exposed {

    private static final Map<String, DescriptorExposedEntity> EXPOSED_ENTITY = new HashMap<>();

    private static void putExposedEntity(String entityname, DescriptorExposedEntity descriptorExposedEntity) {
        EXPOSED_ENTITY.put(entityname.toLowerCase(), descriptorExposedEntity);
    }

    public static boolean isExposed(Class classe) {
        String simpleName = classe.getSimpleName();
        return EXPOSED_ENTITY.containsKey(simpleName.toLowerCase());
    }

    public static DescriptorExposedEntity getExposedEntity(String entityName) {
        return EXPOSED_ENTITY.get(entityName.toLowerCase());
    }

    public static Field getExposedEntityField(String entityNameField) {
        String entityName = entityNameField;
        String strfield = entityNameField;

        if (entityNameField.contains(".")) {
            String[] split = entityNameField.split("\\.");
            entityName = split[0];
            strfield = split[1];
        }
        if (entityNameField.contains("/")) {
            String[] split = entityNameField.split("/");
            entityName = split[0];
            strfield = split[1];
        }
        DescriptorExposedEntity desc = exposed.getExposedEntity(entityName);
        Field field = null;

        if (desc != null) {
            try {
                Class classExposed = desc.getClassExposed();
                field = classExposed.getDeclaredField(strfield);;
            } catch (SecurityException ex) {
                //log.error("Falha de segurança");
            } catch (Exception ex) {

            }
        }
        return field;
    }

    /* Exposed entities configuration */
    static {
        putExposedEntity("account", new DescriptorExposedEntity(Account.class));

        putExposedEntity("accountTransaction",
                new DescriptorExposedEntity(AccountTransaction.class)
                        .hiddenForm("dateExecuted",
                                "typeTransaction", "actualBalance",
                                "parentTransaction", "childTransactions")
                        .hiddenList("childTransactions")
        );

        putExposedEntity("address", new DescriptorExposedEntity(Address.class));
        putExposedEntity("budget", new DescriptorExposedEntity(Budget.class));
        putExposedEntity("city", new DescriptorExposedEntity(City.class));
        putExposedEntity("contact", new DescriptorExposedEntity(Contact.class));
        putExposedEntity("email", new DescriptorExposedEntity(Email.class));

        putExposedEntity("financialAccount", new DescriptorExposedEntity(FinancialAccount.class)
                .hidden("parentAccount", "type", "uniqueCode")
        );
        putExposedEntity("financialCharge", new DescriptorExposedEntity(FinancialCharge.class).hidden("removed", "status"));
        putExposedEntity("billetTicketAgreement",
                new DescriptorExposedEntity(BilletTicketAgreement.class)
                        .hidden("maskNumber", "defaultAgreement", "registerRequeried", "removed")
        );
        putExposedEntity("billetTicket",
                new DescriptorExposedEntity(BilletTicket.class)
        );

        putExposedEntity("genericType", new DescriptorExposedEntity(GenericType.class));
        putExposedEntity("genericValue", new DescriptorExposedEntity(GenericValue.class));

        putExposedEntity("inventory", new DescriptorExposedEntity(Inventory.class));

        putExposedEntity("organization",
                new DescriptorExposedEntity(Organization.class).hidden("logo", "contact")
        );

        putExposedEntity("partner",
                new DescriptorExposedEntity(Partner.class)
                        .hidden("phoneticName", "attribute", "contact", "infoExtra")
        );

        putExposedEntity("infoExtra",
                new DescriptorExposedEntity(InfoExtra.class)
        );

        putExposedEntity("inventory", new DescriptorExposedEntity(Inventory.class));

        putExposedEntity("phone", new DescriptorExposedEntity(Phone.class));
        putExposedEntity("product", new DescriptorExposedEntity(Product.class));

        putExposedEntity("purchaseOrder",
                new DescriptorExposedEntity(PurchaseOrder.class)
                        .hidden("lastModified", "userIdModified", "infoExtra")
                        .hiddenList("itens", "inventory")
        );
        putExposedEntity("purchaseOrderItem", new DescriptorExposedEntity(PurchaseOrderItem.class));

        putExposedEntity("userMessage", new DescriptorExposedEntity(UserMessage.class));
        putExposedEntity("entityDummy", new DescriptorExposedEntity(EntityDummy.class));

        putExposedEntity("task", new DescriptorExposedEntity(Task.class)
                .hiddenForm("tags", "childrens").hiddenList("tags", "childrens")
        );

        putExposedEntity("binaryFile", new DescriptorExposedEntity(BinaryFile.class)
                .hidden("fileBinary", "md5", "pathCloud", "pathLog"));

        /* Temporario, migrar pra outro elemtno */
    }
}

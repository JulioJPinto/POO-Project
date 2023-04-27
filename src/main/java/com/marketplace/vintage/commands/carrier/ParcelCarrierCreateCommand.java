package com.marketplace.vintage.commands.carrier;

import com.marketplace.vintage.VintageConstants;
import com.marketplace.vintage.carrier.ParcelCarrier;
import com.marketplace.vintage.carrier.ParcelCarrierFactory;
import com.marketplace.vintage.carrier.ParcelCarrierManager;
import com.marketplace.vintage.command.BaseCommand;
import com.marketplace.vintage.expression.ExpressionSolver;
import com.marketplace.vintage.input.InputMapper;
import com.marketplace.vintage.logging.Logger;
import com.marketplace.vintage.utils.StringUtils;

public class ParcelCarrierCreateCommand extends BaseCommand {

    private final ParcelCarrierManager parcelCarrierManager;
    private final ExpressionSolver expressionSolver;

    public ParcelCarrierCreateCommand(ParcelCarrierManager parcelCarrierManager, ExpressionSolver expressionSolver) {
        super("create", "carrier create <carrier name> (premium)", 1, "Create a new parcel carrier");
        this.parcelCarrierManager = parcelCarrierManager;
        this.expressionSolver = expressionSolver;
    }

    @Override
    protected void executeSafely(Logger logger, String[] args) {
        String parcelCarrierName = args[0];

        boolean premium = args.length > 1 && args[1].equalsIgnoreCase("premium");

        ParcelCarrier parcelCarrier = premium ?
                                      ParcelCarrierFactory.createPremiumParcelCarrier(parcelCarrierName) :
                                      ParcelCarrierFactory.createNormalParcelCarrier(parcelCarrierName);

        logger.info("Do you want to set a custom expedition price expression? (y/n)");
        logger.info("The default one is: " + VintageConstants.DEFAULT_EXPEDITION_PRICE_EXPRESSION_STRING);
        boolean response = getInputPrompter().askForInput(logger, "Boolean >", InputMapper.BOOLEAN);

        if (response) {
            logger.info("Please enter the expression using the following variables: ");
            logger.info(StringUtils.joinQuoted(VintageConstants.DEFAULT_EXPEDITION_PRICE_EXPRESSION_VARIABLES, ", "));

            String expression = getInputPrompter().askForInput(logger, "Expression >", InputMapper.ofExpression(expressionSolver, VintageConstants.DEFAULT_EXPEDITION_PRICE_EXPRESSION_VARIABLES));
            parcelCarrier.setExpeditionPriceExpression(expression);
        }

        try {
            parcelCarrierManager.registerParcelCarrier(parcelCarrier);
        } catch (Exception e) {
            logger.warn("Failed to create parcel carrier: " + e.getMessage());
            return;
        }

        if (premium) {
            logger.info("Premium parcel carrier " + parcelCarrier.getName() + " created successfully");
        } else {
            logger.info("Parcel carrier " + parcelCarrier.getName() + " created successfully");
        }
    }

}

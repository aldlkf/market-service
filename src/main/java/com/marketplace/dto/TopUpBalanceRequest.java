package com.marketplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Запрос на пополнение баланса")
public class TopUpBalanceRequest{

    @NotNull(message = "Сумма пополнения обязательна")
    @DecimalMin(value = "1.00", message = "Минимальная сумма пополнения — 1.00")
    @Schema(description = "Сумма пополнения", example = "50000.00")
    private BigDecimal amount;

    public TopUpBalanceRequest(){}

    public TopUpBalanceRequest(BigDecimal amount){
        this.amount = amount;
    }

    public BigDecimal getAmount(){
        return amount;
    }

    public void setAmount(BigDecimal amount){
        this.amount = amount;
    }
}
package com.reservation.processor;

import com.reservation.entity.Cancellation;
import com.reservation.entity.RefundStatus;
import org.slf4j.Logger;
import org.jspecify.annotations.Nullable;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import java.time.LocalDateTime;

public class CancellationItemProcessor implements ItemProcessor<Cancellation , Cancellation> {
    private static final Logger log = LoggerFactory.getLogger(CancellationItemProcessor.class);
    @Override
    public @Nullable Cancellation process(Cancellation item) throws Exception {
        if (item.getCancelledAt().isBefore(LocalDateTime.now().minusHours(48)))
            item.setRefundStatus(RefundStatus.OVERDUE);

        return item;
    }
}

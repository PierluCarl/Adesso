package com.adesso.awesome_pizza.orders.model.enumeration;

import java.util.EnumSet;

public enum OrderStatus {
	
	NEW {
		@Override
		public boolean isPreviousStatusAllowed(OrderStatus status) {
			return false;
		}
	},
	IN_PROGRESS {
		@Override
		public boolean isPreviousStatusAllowed(OrderStatus status) {
			return NEW.equals(status);
		}
	},
	COMPLETED {
		@Override
		public boolean isPreviousStatusAllowed(OrderStatus status) {
			return IN_PROGRESS.equals(status);
		}
	},
	CANCELED {
		@Override
		public boolean isPreviousStatusAllowed(OrderStatus status) {
			return EnumSet.of(NEW, IN_PROGRESS).contains(status);
		}
	};
	
	public abstract boolean isPreviousStatusAllowed(OrderStatus status);
}

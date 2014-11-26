package de.bund.bfr.gnuml;

class NuMLException extends RuntimeException {
	def errors = []
	
    def NuMLException() {
        super();
    }

    def NuMLException(String s) {
        super(s);
    }

    def NuMLException(String s, Throwable throwable) {
        super(s, throwable);
    }

    def NuMLException(Throwable throwable) {
        super(throwable);
    }

	@Override
	String getMessage() {
		"${super.getMessage()}:\n${errors.join('\n')}"
	}
}
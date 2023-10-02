package life.offonoff.ab.web.common;

import life.offonoff.ab.exception.AbCode;

public record ErrorWrapper(AbCode abCode, ErrorContent errorContent) {
}

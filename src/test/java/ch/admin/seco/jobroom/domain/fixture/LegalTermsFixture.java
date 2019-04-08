package ch.admin.seco.jobroom.domain.fixture;

import ch.admin.seco.jobroom.domain.LegalTerms;

import static java.time.LocalDate.now;

public class LegalTermsFixture {

    public static LegalTerms testLegalTermsFixture() {
        return new LegalTerms()
            .setEffectiveAt(now())
            .setLinkDe("http://link-de.example.com/")
            .setLinkEn("http://link-en.example.com/")
            .setLinkFr("http://link-fr.example.com/")
            .setLinkIt("http://link-it.example.com/");
    }
}


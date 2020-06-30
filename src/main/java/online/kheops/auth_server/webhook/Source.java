package online.kheops.auth_server.webhook;

import online.kheops.auth_server.entity.Capability;
import online.kheops.auth_server.entity.ReportProvider;
import online.kheops.auth_server.entity.User;

import java.util.Objects;
import java.util.Optional;

public class Source {
    private Optional<ReportProvider> reportProvider = Optional.empty();
    private Optional<Capability> capabilityToken = Optional.empty();
    private Optional<User> user = Optional.empty();

    public Source() { }

    public void setReportProviderClientId(ReportProvider reportProvider) {
        this.reportProvider = Optional.of(reportProvider);
    }

    public void setCapabilityToken(Capability capabilityToken) {
        this.capabilityToken = Optional.of(capabilityToken);
    }

    public void setUser(User user) {
        this.user = Optional.of(user);
    }

    public Optional<ReportProvider> getReportProvider() {
        return reportProvider;
    }

    public Optional<Capability> getCapabilityTokenId() {
        return capabilityToken;
    }

    public Optional<User> getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Source source = (Source) o;
        return reportProvider.equals(source.reportProvider) &&
                capabilityToken.equals(source.capabilityToken) &&
                user.equals(source.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportProvider, capabilityToken, user);
    }

    @Override
    public String toString() {
        String s = "\n\t\tSource{";
        if (reportProvider.isPresent()) {
            s += reportProvider.get().toString();
        }
        if (capabilityToken.isPresent()) {
            s += capabilityToken.get().toString();
        }
        if (user.isPresent()) {
            s += user.get().toString();
        }

        return s += '}';
    }
}

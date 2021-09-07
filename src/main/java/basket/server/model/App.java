package basket.server.model;

import basket.api.util.Version;
import java.net.URL;

public record App(String name,
                  String description,
                  Version stable,
                  Version experimental,
                  URL iconAddress,
                  URL githubHome) {}

# Headers

geOrchestra Gateway adds specific HTTP headers to requests forwarded to backend applications. These headers provide information about the authenticated user and their organization.

## Default Headers

The gateway adds the following default headers to all requests:

| Header | Description | Example Value |
|--------|-------------|---------------|
| `sec-proxy` | Indicates the request comes from the proxy | `true` |
| `sec-username` | The user's username (not provided for anonymous users) | `admin` |
| `sec-roles` | Semi-colon separated list of roles (not provided for anonymous users) | `ADMINISTRATOR;USER` |
| `sec-org` | The organization ID (LDAP's `cn`) | `psc` |
| `sec-orgname` | The human-readable organization name | `Project Steering Committee` |
| `sec-email` | The user's email address | `admin@georchestra.org` |
| `sec-firstname` | The user's first name (LDAP `givenName`) | `Admin` |
| `sec-lastname` | The user's last name (LDAP `sn`) | `Istrator` |
| `sec-tel` | The user's telephone number (LDAP `telephoneNumber`) | `+33123456789` |
| `sec-json-user` | Base64-encoded JSON representation of the user object | (Base64-encoded JSON) |
| `sec-json-organization` | Base64-encoded JSON representation of the organization object | (Base64-encoded JSON) |

## Configuring Headers

You can configure which headers are sent to each service in the `gateway.yaml` file:

```yaml
georchestra:
  gateway:
    default-headers:
      proxy: true
      username: true
      roles: true
      org: true
      orgname: true
      email: true
      firstname: true
      lastname: true
      tel: true
      json-user: true
      json-organization: true
```

### Service-Specific Headers

You can override the default headers for specific services:

```yaml
georchestra:
  gateway:
    services:
      myapp:
        headers:
          proxy: true
          username: true
          roles: false
          org: false
          # Other headers...
```

In this example, the `myapp` service will receive the `sec-proxy` and `sec-username` headers, but not the `sec-roles` or `sec-org` headers.

## Header Mappings

The header mappings are defined in the `HeaderMappings` class:

```java
public class HeaderMappings {

    private boolean proxy = true;
    private boolean username = true;
    private boolean roles = true;
    private boolean org = true;
    private boolean orgname = true;
    private boolean email = true;
    private boolean firstname = true;
    private boolean lastname = true;
    private boolean tel = true;
    private boolean jsonUser = true;
    private boolean jsonOrganization = true;
    // ...
}
```

To configure header mappings in YAML, convert the camelCase property names to kebab-case:

```yaml
headers:
  proxy: true          # camelCase: proxy
  username: true       # camelCase: username
  roles: true          # camelCase: roles
  org: true            # camelCase: org
  orgname: true        # camelCase: orgname
  email: true          # camelCase: email
  firstname: true      # camelCase: firstname
  lastname: true       # camelCase: lastname
  tel: true            # camelCase: tel
  json-user: true      # camelCase: jsonUser
  json-organization: true # camelCase: jsonOrganization
```

## Role Mappings

The Gateway can add additional roles to users based on their existing roles. This is configured in the `roles-mappings.yaml` file:

```yaml
georchestra:
  gateway:
    role-mappings:
      '[ROLE_GP.GDI.*]':
        - ROLE_USER
      '[ROLE_GP.GDI.ADMINISTRATOR]':
        - ROLE_ADMINISTRATOR
```

In this example:

- Users with any role starting with `ROLE_GP.GDI.` will also get the `ROLE_USER` role
- Users with the specific role `ROLE_GP.GDI.ADMINISTRATOR` will also get the `ROLE_ADMINISTRATOR` role

### Wildcard Support

Limited regular expression support is available for role mappings. Only the `*` character is allowed as a wildcard on a source role name.

Note that for key names (source roles) to include special characters, you must use the format `'[role.name.*]'` for the literal string `role.name.*` to be interpreted correctly.

### Multiple Mappings

If an authentication provider role name matches multiple mappings, all the matching additional roles will be appended.

For example, with the mappings shown above, a user with the role `ROLE_GP.GDI.ADMINISTRATOR` will receive both the `ROLE_USER` and `ROLE_ADMINISTRATOR` roles, while users with other roles starting with `ROLE_GP.GDI.` will only receive the `ROLE_USER` role.

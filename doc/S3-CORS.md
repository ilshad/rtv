How to enable Cross-Origin-Request on Amazon S3

Set this in AWS S3 management console. Right-click on the desired
bucket and select Properties. Below, on the permissions tab, click
Edit CORS configuration, paste the XML below and click Save.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<CORSConfiguration xmlns="http://s3.amazonaws.com/doc/2006-03-01/">
    <CORSRule>
        <AllowedOrigin>*</AllowedOrigin>
        <AllowedMethod>GET</AllowedMethod>
        <MaxAgeSeconds>3000</MaxAgeSeconds>
        <AllowedHeader>Authorization</AllowedHeader>
    </CORSRule>
    <CORSRule>
        <AllowedOrigin>*</AllowedOrigin>
        <AllowedMethod>PUT</AllowedMethod>
        <MaxAgeSeconds>3000</MaxAgeSeconds>
        <AllowedHeader>Content-Type</AllowedHeader>
        <AllowedHeader>x-amz-acl</AllowedHeader>
        <AllowedHeader>origin</AllowedHeader>
    </CORSRule>
</CORSConfiguration>
```

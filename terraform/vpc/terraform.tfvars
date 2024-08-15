####################### VPC #####################
networks = {
  cidr_block = "10.0.0.0/24"
  name       = "vpc-neoris"
  igw_tag    = "IG-neoris"
}

subnets = {
  "Private A" = {
    cidr_block        = "10.0.0.0/26"
    availability_zone = "us-east-1a"
    is_public         = false
  }
  "Public A" = {
    cidr_block        = "10.0.0.64/26"
    availability_zone = "us-east-1a"
    is_public         = true
    tag_eip           = { Name = "IP A Neoris" }
    tag_nat_gateway   = { Name = "NAT A Neoris" }
  }
  "Private B" = {
    cidr_block        = "10.0.0.128/26"
    availability_zone = "us-east-1b"
    is_public         = false
  }
  "Public B" = {
    cidr_block        = "10.0.0.192/26"
    availability_zone = "us-east-1b"
    is_public         = true
    tag_eip           = { Name = "IP B Neoris" }
    tag_nat_gateway   = { Name = "NAT B Neoris" }
  }
}

name_rt_public = "rt-public-neoris"

tags = {
  ManagedBy = "Terraform"
  Account   = "Neoris"
  Owner     = "Neoris"
}

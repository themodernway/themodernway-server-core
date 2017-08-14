/*
 * Copyright (c) 2017, The Modern Way. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.themodernway.server.core.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.common.api.types.IBuilder;

public interface IAuthorizationSolver extends IRolesPredicate
{
    public static class Builder implements IBuilder<IAuthorizationSolver>
    {
        private final List<IRolesPredicate> m_list = new ArrayList<IRolesPredicate>();

        protected Builder()
        {
        }

        public static Builder make()
        {
            return new Builder();
        }

        public Builder all(final String... roles)
        {
            return all(Arrays.asList(roles));
        }

        public Builder all(final List<String> roles)
        {
            m_list.add(new AllRolesOp(roles));

            return this;
        }

        public Builder any(final String... roles)
        {
            return any(Arrays.asList(roles));
        }

        public Builder any(final List<String> roles)
        {
            m_list.add(new AnyRolesOp(roles));

            return this;
        }

        public Builder none(final String... roles)
        {
            return none(Arrays.asList(roles));
        }

        public Builder none(final List<String> roles)
        {
            m_list.add(new NoneRolesOp(roles));

            return this;
        }

        public Builder and(final IRolesPredicate... predicates)
        {
            return and(Arrays.asList(predicates));
        }

        public Builder and(final List<IRolesPredicate> predicates)
        {
            m_list.add(new AndPredicatesOp(predicates));

            return this;
        }

        public Builder or(final IRolesPredicate... predicates)
        {
            return or(Arrays.asList(predicates));
        }

        public Builder or(final List<IRolesPredicate> predicates)
        {
            m_list.add(new OrPredicatesOp(predicates));

            return this;
        }

        public Builder not(final IRolesPredicate predicate)
        {
            m_list.add((roles) -> false == predicate.test(roles));

            return this;
        }

        @Override
        public IAuthorizationSolver build()
        {
            final List<IRolesPredicate> list = m_list;

            return new IAuthorizationSolver()
            {
                @Override
                public final boolean test(final List<String> roles)
                {
                    for (final IRolesPredicate pred : list)
                    {
                        if (false == pred.test(roles))
                        {
                            return false;
                        }
                    }
                    return true;
                }
            };
        }

        protected abstract static class AbstractRolesOp implements IRolesPredicate
        {
            private final List<String> m_roles;

            protected AbstractRolesOp(final List<String> roles)
            {
                m_roles = StringOps.toUnique(roles);
            }

            protected final List<String> getRoles()
            {
                return Collections.unmodifiableList(m_roles);
            }
        }

        protected static class AllRolesOp extends AbstractRolesOp
        {
            protected AllRolesOp(final List<String> roles)
            {
                super(roles);
            }

            @Override
            public boolean test(final List<String> list)
            {
                for (final String role : getRoles())
                {
                    if (false == list.contains(role))
                    {
                        return false;
                    }
                }
                return true;
            }
        }

        protected static class AnyRolesOp extends AbstractRolesOp
        {
            protected AnyRolesOp(final List<String> roles)
            {
                super(roles);
            }

            @Override
            public boolean test(final List<String> list)
            {
                for (final String role : getRoles())
                {
                    if (list.contains(role))
                    {
                        return true;
                    }
                }
                return false;
            }
        }

        protected static class NoneRolesOp extends AbstractRolesOp
        {
            protected NoneRolesOp(final List<String> roles)
            {
                super(roles);
            }

            @Override
            public boolean test(final List<String> list)
            {
                for (final String role : getRoles())
                {
                    if (list.contains(role))
                    {
                        return false;
                    }
                }
                return true;
            }
        }

        protected abstract static class AbstractPredicatesOp implements IRolesPredicate
        {
            private final List<IRolesPredicate> m_predicates;

            protected AbstractPredicatesOp(final List<IRolesPredicate> predicates)
            {
                m_predicates = predicates;
            }

            protected final List<IRolesPredicate> getPredicates()
            {
                return Collections.unmodifiableList(m_predicates);
            }
        }

        protected static class AndPredicatesOp extends AbstractPredicatesOp
        {
            protected AndPredicatesOp(final List<IRolesPredicate> predicates)
            {
                super(predicates);
            }

            @Override
            public boolean test(final List<String> roles)
            {
                for (final IRolesPredicate predicate : getPredicates())
                {
                    if (false == predicate.test(roles))
                    {
                        return false;
                    }
                }
                return true;
            }
        }

        protected static class OrPredicatesOp extends AbstractPredicatesOp
        {
            protected OrPredicatesOp(final List<IRolesPredicate> predicates)
            {
                super(predicates);
            }

            @Override
            public boolean test(final List<String> roles)
            {
                for (final IRolesPredicate predicate : getPredicates())
                {
                    if (predicate.test(roles))
                    {
                        return true;
                    }
                }
                return false;
            }
        }
    }
}
